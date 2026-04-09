package com.supervision.livraison.service;

import com.supervision.livraison.dto.NotificationDTO;
import com.supervision.livraison.entity.Commande;
import com.supervision.livraison.entity.Notification;
import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.repository.CommandeRepository;
import com.supervision.livraison.repository.NotificationRepository;
import com.supervision.livraison.repository.PersonnelRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification service — manages notifications between personnel.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PersonnelRepository personnelRepository;
    private final CommandeRepository commandeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               PersonnelRepository personnelRepository,
                               CommandeRepository commandeRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.personnelRepository = personnelRepository;
        this.commandeRepository = commandeRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Get all notifications for a receiver.
     */
    public List<NotificationDTO> getNotificationsByReceiver(Long receiverId) {
        return notificationRepository.findByReceiverId(receiverId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications count for a receiver.
     */
    public Long getUnreadCount(Long receiverId) {
        return notificationRepository.countUnreadByReceiverId(receiverId);
    }

    /**
     * Create a new notification and send via WebSocket.
     */
    @Transactional
    public NotificationDTO createNotification(Long senderId, Long receiverId, String message, String type, Long nocde) {
        Notification notification = new Notification();

        Personnel sender = personnelRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        Personnel receiver = personnelRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        if (nocde != null) {
            Commande commande = commandeRepository.findById(nocde)
                    .orElseThrow(() -> new RuntimeException("Commande not found"));
            notification.setCommande(commande);
        }

        Notification saved = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                receiver.getLogin(),
                "/queue/notifications",
                toDTO(saved)
        );

        return toDTO(saved);
    }

    /**
     * Mark a notification as read.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for a receiver.
     */
    @Transactional
    public void markAllAsRead(Long receiverId) {
        List<Notification> unread = notificationRepository.findUnreadByReceiverId(receiverId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Convert Notification entity to DTO.
     */
    private NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedAt(n.getCreatedAt());

        if (n.getSender() != null) {
            dto.setSenderId(n.getSender().getIdpers());
            dto.setSenderName(n.getSender().getNompers() + " " + n.getSender().getPrenompers());
        }
        if (n.getReceiver() != null) {
            dto.setReceiverId(n.getReceiver().getIdpers());
            dto.setReceiverName(n.getReceiver().getNompers() + " " + n.getReceiver().getPrenompers());
        }
        if (n.getCommande() != null) {
            dto.setNocde(n.getCommande().getNocde());
        }

        return dto;
    }
}
