package com.supervision.livraison.controller;

import com.supervision.livraison.dto.NotificationDTO;
import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Notification controller — manages notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET /api/notifications — Get all notifications for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(@AuthenticationPrincipal Personnel user) {
        return ResponseEntity.ok(notificationService.getNotificationsByReceiver(user.getIdpers()));
    }

    /**
     * GET /api/notifications/unread-count — Get unread notification count.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal Personnel user) {
        return ResponseEntity.ok(notificationService.getUnreadCount(user.getIdpers()));
    }

    /**
     * POST /api/notifications — Create a new notification.
     */
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @AuthenticationPrincipal Personnel sender,
            @RequestBody Map<String, Object> request) {

        Long receiverId = Long.valueOf(request.get("receiverId").toString());
        String message = (String) request.get("message");
        String type = (String) request.get("type");
        Long nocde = request.get("nocde") != null ? Long.valueOf(request.get("nocde").toString()) : null;

        return ResponseEntity.ok(notificationService.createNotification(sender.getIdpers(), receiverId, message, type, nocde));
    }

    /**
     * PUT /api/notifications/{id}/read — Mark notification as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/notifications/read-all — Mark all notifications as read.
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Personnel user) {
        notificationService.markAllAsRead(user.getIdpers());
        return ResponseEntity.ok().build();
    }
}
