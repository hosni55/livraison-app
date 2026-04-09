package com.supervision.livraison.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Firebase configuration — initializes Firebase Admin SDK for FCM push notifications.
 * Requires firebase-service-account.json in the resources/ directory.
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path:classpath:firebase-service-account.json}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            // Only initialize if Firebase hasn't been initialized yet
            if (FirebaseApp.getApps().isEmpty()) {
                ClassPathResource serviceAccount = new ClassPathResource("firebase-service-account.json");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully.");
            }
        } catch (IOException e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
            System.err.println("Push notifications will not be available until firebase-service-account.json is provided.");
        } catch (IllegalStateException e) {
            // Firebase already initialized
            System.out.println("Firebase already initialized.");
        }
    }
}
