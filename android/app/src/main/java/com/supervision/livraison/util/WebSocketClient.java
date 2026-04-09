package com.supervision.livraison.util;

import android.util.Log;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocket client for real-time communication with the backend.
 * Handles connection, reconnection, and message sending.
 *
 * Usage:
 *   WebSocketClient ws = new WebSocketClient(context);
 *   ws.connect();
 *   ws.sendMessage("/app/chat.send", payload);
 *   ws.setListener(message -> { ... });
 */
public class WebSocketClient {

    private static final String TAG = "WebSocketClient";
    // Use your PC local IP when running on a real Android device
    // Example: ws://192.168.1.16:8080/ws/websocket
    private static final String WS_URL = "ws://192.168.1.16:8080/ws/websocket";

    private org.java_websocket.client.WebSocketClient client;
    private MessageListener listener;
    private boolean isConnected = false;

    public interface MessageListener {
        void onMessage(String message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    /**
     * Connect to the WebSocket server.
     */
    public void connect() {
        try {
            URI uri = new URI(WS_URL);
            client = new org.java_websocket.client.WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "WebSocket connected");
                    isConnected = true;
                    if (listener != null) listener.onConnected();
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "WebSocket message: " + message);
                    if (listener != null) listener.onMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket closed: " + reason);
                    isConnected = false;
                    if (listener != null) listener.onDisconnected();
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "WebSocket error: " + ex.getMessage());
                    isConnected = false;
                    if (listener != null) listener.onError(ex.getMessage());
                }
            };

            client.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid WebSocket URI: " + e.getMessage());
            if (listener != null) listener.onError(e.getMessage());
        }
    }

    /**
     * Send a message to the WebSocket server.
     */
    public void sendMessage(String message) {
        if (client != null && client.isOpen()) {
            client.send(message);
        } else {
            Log.w(TAG, "Cannot send message: WebSocket not connected");
        }
    }

    /**
     * Disconnect from the WebSocket server.
     */
    public void disconnect() {
        if (client != null && client.isOpen()) {
            client.close();
        }
    }

    /**
     * Check if connected.
     */
    public boolean isConnected() {
        return isConnected;
    }
}
