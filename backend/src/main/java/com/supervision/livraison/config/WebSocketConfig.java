package com.supervision.livraison.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration using STOMP protocol.
 * Enables real-time messaging between livreurs and controleurs.
 *
 * Endpoints:
 * - /ws — STOMP endpoint for SockJS fallback
 * - /topic — broker prefix for broadcast messages (e.g., /topic/gps-updates)
 * - /queue — broker prefix for point-to-point messages (e.g., /queue/messages)
 * - /app — application destination prefix for sending messages
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker for broadcasting
        config.enableSimpleBroker("/topic", "/queue");
        // Set prefix for messages sent to the application (controllers)
        config.setApplicationDestinationPrefixes("/app");
        // Set prefix for user-specific queues
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint with SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
