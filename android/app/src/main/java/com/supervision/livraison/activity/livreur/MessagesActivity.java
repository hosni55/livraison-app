package com.supervision.livraison.activity.livreur;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.supervision.livraison.R;
import com.supervision.livraison.util.SessionManager;
import com.supervision.livraison.util.WebSocketClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MessagesActivity — chat with controleur via WebSocket.
 * Includes quick message templates for common scenarios.
 */
public class MessagesActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private WebSocketClient wsClient;
    private ScrollView scrollMessages;
    private LinearLayout layoutMessages;
    private EditText etMessage;
    private Button btnSend;
    private LinearLayout layoutQuickMessages;

    // Quick message templates
    private String[] QUICK_MESSAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        sessionManager = new SessionManager(this);
        
        // Define messages based on role
        if ("CONTROLEUR".equalsIgnoreCase(sessionManager.getRole())) {
            QUICK_MESSAGES = new String[]{
                "Attention: Nouvelle livraison pour vous",
                "Merci de valider votre statut",
                "Information: Trafic dense sur votre zone",
                "Appel urgent: Contactez le superviseur"
            };
        } else {
            QUICK_MESSAGES = new String[]{
                "Client absent — je réessaie plus tard",
                "Client ne répond pas au téléphone",
                "Le client n'accepte pas la commande",
                "Adresse introuvable",
                "Besoin d'aide urgente"
            };
        }

        scrollMessages = findViewById(R.id.scroll_messages);
        layoutMessages = findViewById(R.id.layout_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        layoutQuickMessages = findViewById(R.id.layout_quick_messages);

        // Setup quick message buttons
        setupQuickMessages();

        // Handle Emergency Context
        if (getIntent().getBooleanExtra("emergency", false)) {
            long nocde = getIntent().getLongExtra("nocde", -1);
            String clientTel = getIntent().getStringExtra("client_tel");
            String emergencyHeader = "[URGENCE] Commande #" + nocde + " - Tel: " + clientTel + "\n";
            etMessage.setText(emergencyHeader);
            etMessage.setSelection(etMessage.getText().length());
        }

        // Setup send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Connect to WebSocket
        wsClient = new WebSocketClient();
        wsClient.setListener(new WebSocketClient.MessageListener() {
            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        Map<String, Object> data = gson.fromJson(message, Map.class);
                        if (data.containsKey("content")) {
                            addMessageToUI(
                                    (String) data.get("senderName"),
                                    (String) data.get("content"),
                                    false
                            );
                        }
                    } catch (Exception e) {
                        addMessageToUI("Système", message, false);
                    }
                });
            }

            @Override
            public void onConnected() {
                runOnUiThread(() -> Toast.makeText(MessagesActivity.this, "Connecté au chat", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> Toast.makeText(MessagesActivity.this, "Déconnecté du chat", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MessagesActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show());
            }
        });
        wsClient.connect();
    }

    private void setupQuickMessages() {
        for (String quickMsg : QUICK_MESSAGES) {
            Button btn = new Button(this);
            btn.setText(quickMsg);
            btn.setTextSize(12);
            btn.setPadding(16, 8, 16, 8);
            btn.setOnClickListener(v -> {
                etMessage.setText(quickMsg);
                sendMessage();
            });
            layoutQuickMessages.addView(btn);
        }
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) return;

        // Build WebSocket message payload
        Map<String, Object> payload = new HashMap<>();
        
        // Controller speaks to drivers (Broadcast or last sender), Drivers speak to controller
        if ("CONTROLEUR".equalsIgnoreCase(sessionManager.getRole())) {
            payload.put("receiverId", 0L); // 0L represents broadcast to all drivers
        } else {
            payload.put("receiverId", 6L); // Specific controller ID
        }
        
        payload.put("content", message);

        Gson gson = new Gson();
        wsClient.sendMessage(gson.toJson(payload));

        addMessageToUI("Vous", message, true);
        etMessage.setText("");
    }

    private void addMessageToUI(String sender, String content, boolean isMe) {
        TextView tv = new TextView(this);
        tv.setText(sender + " — " + content);
        tv.setTextSize(14);
        tv.setPadding(16, 8, 16, 8);
        tv.setBackgroundResource(isMe ? R.drawable.bg_message_sent : R.drawable.bg_message_received);
        tv.setTextColor(isMe ? android.graphics.Color.WHITE : android.graphics.Color.BLACK);

        layoutMessages.addView(tv);

        // Auto-scroll to bottom
        scrollMessages.post(() -> scrollMessages.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wsClient != null) {
            wsClient.disconnect();
        }
    }
}
