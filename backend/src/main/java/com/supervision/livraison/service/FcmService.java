package com.supervision.livraison.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Firebase Cloud Messaging service — sends push notifications to Android devices.
 * 
 * Requires firebase-service-account.json in resources/ directory.
 * Initialize Firebase Admin SDK in a @PostConstruct method.
 */
@Service
public class FcmService {

    private boolean firebaseInitialized = false;

    /**
     * Send a push notification to a specific device token.
     * 
     * @param deviceToken FCM registration token of the target device
     * @param title Notification title
     * @param body Notification body
     * @param data Additional key-value data payload
     */
    public void sendNotification(String deviceToken, String title, String body, Map<String, String> data) {
        if (!firebaseInitialized) {
            System.err.println("Firebase not initialized. Skipping notification.");
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            // Add custom data payload
            if (data != null) {
                messageBuilder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            System.out.println("FCM notification sent: " + response);

        } catch (Exception e) {
            System.err.println("Error sending FCM notification: " + e.getMessage());
        }
    }

    /**
     * Send notification when delivery status changes.
     */
    public void sendDeliveryStatusUpdate(String deviceToken, Long nocde, String newStatus) {
        sendNotification(
                deviceToken,
                "Mise à jour livraison",
                "La livraison #" + nocde + " est maintenant: " + newStatus,
                Map.of(
                        "type", "delivery_status",
                        "nocde", nocde.toString(),
                        "status", newStatus
                )
        );
    }

    /**
     * Send notification for urgent message from livreur.
     */
    public void sendUrgentMessageAlert(String deviceToken, String livreurName, String message) {
        sendNotification(
                deviceToken,
                "Message urgent — " + livreurName,
                message,
                Map.of(
                        "type", "urgent_message",
                        "sender", livreurName
                )
        );
    }
}
