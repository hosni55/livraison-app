package com.supervision.livraison.controller;

import com.supervision.livraison.entity.Message;
import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.repository.MessageRepository;
import com.supervision.livraison.repository.PersonnelRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket message controller — handles real-time chat between livreurs and controleurs.
 *
 * Message flow:
 * - Client sends to: /app/chat.send
 * - Server broadcasts to: /topic/chat/{receiverId}
 * - Client subscribes to: /user/queue/messages (for personal messages)
 */
@Controller
public class ChatMessageController {

    private final MessageRepository messageRepository;
    private final PersonnelRepository personnelRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageController(MessageRepository messageRepository,
                                 PersonnelRepository personnelRepository,
                                 SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.personnelRepository = personnelRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle incoming chat messages via WebSocket.
     * Saves to database and forwards to receiver.
     *
     * Expected payload:
     * {
     *   "receiverId": 6,
     *   "content": "Client absent, que faire?",
     *   "nocde": 5
     * }
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> payload,
                            @AuthenticationPrincipal Personnel sender) {

        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = (String) payload.get("content");
        Long nocde = payload.get("nocde") != null ? Long.valueOf(payload.get("nocde").toString()) : null;

        Personnel receiver = personnelRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Save message to database
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        if (nocde != null) {
            message.setCommande(
                    messageRepository.findById(1L).map(m -> m.getCommande()).orElse(null)
            );
        }

        Message saved = messageRepository.save(message);

        // Send to receiver via WebSocket (user-specific queue)
        messagingTemplate.convertAndSendToUser(
                receiver.getLogin(),
                "/queue/messages",
                Map.of(
                        "id", saved.getId(),
                        "senderId", sender.getIdpers(),
                        "senderName", sender.getNompers() + " " + sender.getPrenompers(),
                        "content", saved.getContent(),
                        "nocde", nocde,
                        "createdAt", saved.getCreatedAt().toString()
                )
        );

        // Also send confirmation back to sender
        messagingTemplate.convertAndSendToUser(
                sender.getLogin(),
                "/queue/messages",
                Map.of(
                        "id", saved.getId(),
                        "senderId", sender.getIdpers(),
                        "senderName", sender.getNompers() + " " + sender.getPrenompers(),
                        "content", saved.getContent(),
                        "nocde", nocde,
                        "createdAt", saved.getCreatedAt().toString()
                )
        );
    }

    /**
     * Handle GPS position updates via WebSocket.
     * Broadcasts to all controleurs.
     */
    @MessageMapping("/gps.update")
    @SendTo("/topic/gps-updates")
    public Map<String, Object> updateGps(@Payload Map<String, Object> payload,
                                         @AuthenticationPrincipal Personnel livreur) {
        return Map.of(
                "livreurId", livreur.getIdpers(),
                "livreurName", livreur.getNompers() + " " + livreur.getPrenompers(),
                "latitude", payload.get("latitude"),
                "longitude", payload.get("longitude"),
                "timestamp", System.currentTimeMillis()
        );
    }
}
