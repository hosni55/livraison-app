package com.supervision.livraison.controller;

import com.supervision.livraison.dto.ChatAssistantRequest;
import com.supervision.livraison.dto.DelayPredictionRequest;
import com.supervision.livraison.dto.DelayPredictionResponse;
import com.supervision.livraison.entity.Commande;
import com.supervision.livraison.entity.LivraisonCom;
import com.supervision.livraison.repository.CommandeRepository;
import com.supervision.livraison.repository.LivraisonRepository;
import com.supervision.livraison.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI controller — handles delay prediction (Weka) and driver assistant chatbot (Gemini 2.0 Flash).
 * Gemini 2.0 Flash is FREE: 15 requests/min, 1M tokens/day, no credit card needed.
 * Falls back to rule-based AI if Gemini API key is not configured.
 *
 * Get your free Gemini API key at: https://aistudio.google.com/apikey
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AiService aiService;
    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    private final RestTemplate restTemplate = new RestTemplate();

    public AIController(AiService aiService, LivraisonRepository livraisonRepository, CommandeRepository commandeRepository) {
        this.aiService = aiService;
        this.livraisonRepository = livraisonRepository;
        this.commandeRepository = commandeRepository;
    }

    /**
     * POST /api/ai/predict-delay — Predict delivery delay using Weka RandomForest.
     */
    @PostMapping("/predict-delay")
    public ResponseEntity<DelayPredictionResponse> predictDelay(@RequestBody DelayPredictionRequest request) {
        DelayPredictionResponse response = aiService.predictDelay(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/ai/assistant — Chat with AI assistant for delivery drivers.
     * Uses Google Gemini 2.0 Flash (FREE tier) with fallback to rule-based AI.
     */
    @PostMapping("/assistant")
    public ResponseEntity<Map<String, String>> chatAssistant(@RequestBody ChatAssistantRequest request) {
        String response;

        // Try Gemini first if API key is configured
        if (geminiApiKey != null && !geminiApiKey.isEmpty() && !geminiApiKey.equals("your_gemini_api_key_here")) {
            response = callGemini(request);
        } else {
            // Fallback to free rule-based AI (no API key needed)
            response = getRuleBasedResponse(request);
        }

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        result.put("timestamp", new Date().toString());
        result.put("model", (geminiApiKey != null && !geminiApiKey.isEmpty() && !geminiApiKey.equals("your_gemini_api_key_here")) ? geminiModel : "rule-based-ai");

        return ResponseEntity.ok(result);
    }

    /**
     * Call Google Gemini 2.0 Flash API (FREE tier).
     * Uses the REST API directly — no SDK needed.
     */
    private String callGemini(ChatAssistantRequest request) {
        try {
            String systemPrompt = buildGeminiPrompt(request);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Build Gemini request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(Map.of("text", systemPrompt + "\n\nUser message: " + request.getMessage()));
            content.put("parts", parts);
            content.put("role", "user");
            requestBody.put("contents", List.of(content));

            // Generation config
            Map<String, Object> config = new HashMap<>();
            config.put("temperature", 0.7);
            config.put("maxOutputTokens", 500);
            config.put("topP", 0.8);
            requestBody.put("generationConfig", config);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentResp = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> partsResp = (List<Map<String, Object>>) contentResp.get("parts");
                    if (!partsResp.isEmpty()) {
                        return (String) partsResp.get(0).get("text");
                    }
                }
            }

            return "Erreur: Pas de réponse de Gemini.";

        } catch (Exception e) {
            // Fallback to rule-based AI if Gemini fails
            return getRuleBasedResponse(request) + "\n\n⚠️ _Gemini API indisponible, mode hors-ligne activé._";
        }
    }

    /**
     * Build the system prompt for Gemini with delivery context.
     */
    private String buildGeminiPrompt(ChatAssistantRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Tu es un assistant intelligent pour les livreurs professionnels. ");
        prompt.append("Tu aides à résoudre les problèmes de livraison rapidement et professionnellement. ");
        prompt.append("Réponds TOUJOURS en français. Sois concis, pratique et utilise des emojis pour la clarté.\n\n");

        // Add delivery context if available
        if (request.getNocde() != null) {
            LivraisonCom livraison = livraisonRepository.findById(request.getNocde()).orElse(null);
            if (livraison != null && livraison.getCommande() != null) {
                Commande commande = livraison.getCommande();
                if (commande.getClient() != null) {
                    prompt.append("📋 **Contexte de livraison actuel:**\n");
                    prompt.append("- Commande #: ").append(request.getNocde()).append("\n");
                    prompt.append("- Client: ").append(commande.getClient().getNomclt())
                          .append(" ").append(commande.getClient().getPrenomclt()).append("\n");
                    prompt.append("- Adresse: ").append(commande.getClient().getAdrclt())
                          .append(", ").append(commande.getClient().getVilleclt()).append("\n");
                    prompt.append("- Téléphone client: ").append(commande.getClient().getTelclt()).append("\n");
                    prompt.append("- Statut livraison: ").append(livraison.getEtatliv()).append("\n\n");
                }
            }
        }

        prompt.append("Guide le livreur avec des étapes numérotées et des conseils pratiques. ");
        prompt.append("Si le livreur décrit un problème, donne une procédure claire étape par étape.\n");
        prompt.append("Suggestions de messages rapides:\n");
        prompt.append("- \"Client absent\" → Attendre 10 min, appeler, photo preuve, marquer ECHEC\n");
        prompt.append("- \"Client refuse\" → Rester poli, vérifier colis, contacter contrôleur\n");
        prompt.append("- \"Adresse fausse\" → Vérifier Google Maps, appeler client, photo, marquer ECHEC\n");

        return prompt.toString();
    }

    /**
     * Free rule-based AI assistant — fallback when Gemini API is not configured.
     * Analyzes keywords and delivery context to provide intelligent responses.
     */
    private String getRuleBasedResponse(ChatAssistantRequest request) {
        String message = request.getMessage().toLowerCase();
        String contextInfo = getContextInfo(request.getNocde());

        if (containsAny(message, "client absent", "client ne répond", "pas de réponse", "ne répond pas")) {
            return "📋 **Client absent — Procédure recommandée:**\n\n" +
                   "1. ⏱️ Attendez 10 minutes sur place\n" +
                   "2. 📞 Appelez le client au numéro indiqué\n" +
                   "3. 📸 Prenez une photo de l'adresse comme preuve\n" +
                   "4. 📝 Si toujours absent, marquez la livraison comme 'ECHEC' avec remarque 'Client absent'\n" +
                   "5. 🔄 Reprogrammez la livraison pour demain\n\n" +
                   contextInfo +
                   "\n💡 *Conseil: Appelez toujours le client 30 min avant d'arriver.*";
        }

        if (containsAny(message, "client refuse", "refuse livraison", "ne veut pas", "refuse")) {
            return "⚠️ **Client refuse la livraison — Procédure:**\n\n" +
                   "1. 🗣️ Restez poli et professionnel\n" +
                   "2. 📦 Vérifiez que le colis correspond à la commande\n" +
                   "3. 📞 Contactez immédiatement le contrôleur\n" +
                   "4. 📝 Marquez comme 'ECHEC' avec remarque 'Refus client'\n" +
                   "5. 📸 Prenez une photo du colis comme preuve\n\n" +
                   contextInfo +
                   "\n💡 *Ne forcez jamais la livraison. Le contrôleur gérera la situation.*";
        }

        if (containsAny(message, "adresse fausse", "mauvaise adresse", "adresse introuvable", "adresse incorrect")) {
            return "📍 **Adresse introuvable — Procédure:**\n\n" +
                   "1. 🗺️ Vérifiez l'adresse sur Google Maps\n" +
                   "2. 📞 Appelez le client pour confirmer l'adresse exacte\n" +
                   "3. 🏢 Demandez des points de repère proches\n" +
                   "4. 📸 Prenez une photo du lieu si vous êtes sur place\n" +
                   "5. 📝 Si adresse erronée, marquez 'ECHEC' avec remarque 'Adresse incorrecte'\n\n" +
                   contextInfo +
                   "\n💡 *Toujours appeler le client 30 min avant pour confirmer l'adresse.*";
        }

        if (containsAny(message, "colis endommagé", "abîmé", "cassé", "endommagé")) {
            return "🚨 **Colis endommagé — Procédure urgente:**\n\n" +
                   "1. 📸 Prenez plusieurs photos du colis sous différents angles\n" +
                   "2. 📝 Notez les dommages visibles sur le bon de livraison\n" +
                   "3. 📞 Informez immédiatement le contrôleur\n" +
                   "4. ❌ Ne livrez PAS le colis endommagé\n" +
                   "5. 📝 Marquez 'ECHEC' avec remarque 'Colis endommagé'\n\n" +
                   contextInfo +
                   "\n⚠️ *Ne livrez jamais un colis visiblement endommagé.*";
        }

        if (containsAny(message, "report", "reporter", "demain", "plus tard", "changer date")) {
            return "📅 **Report de livraison — Procédure:**\n\n" +
                   "1. 📞 Confirmez la nouvelle date avec le client\n" +
                   "2. 📝 Marquez 'ECHEC' avec remarque 'Report demandé par client - Nouvelle date: [date]'\n" +
                   "3. 📞 Informez le contrôleur du report\n" +
                   "4. 📸 Prenez une photo si vous êtes déjà sur place\n\n" +
                   contextInfo +
                   "\n💡 *Notez toujours la nouvelle date convenue dans la remarque.*";
        }

        if (containsAny(message, "aide", "urgent", "problème", "urgence", "besoin d'aide")) {
            return "🆘 **Assistance urgente:**\n\n" +
                   "1. 📞 Appelez directement le contrôleur par téléphone\n" +
                   "2. 📝 Décrivez le problème en détail\n" +
                   "3. 📸 Prenez des photos si nécessaire\n" +
                   "4. ⏸️ Ne prenez aucune décision sans l'accord du contrôleur\n\n" +
                   contextInfo +
                   "\n📞 *En cas d'urgence, privilégiez toujours l'appel téléphonique.*";
        }

        if (containsAny(message, "paiement", "payer", "espèces", "carte", "combien")) {
            return "💰 **Informations paiement:**\n\n" +
                   "1. 💵 Vérifiez le mode de paiement sur votre application\n" +
                   "2. 🧾 Préparez le reçu de paiement\n" +
                   "3. ✅ Confirmez le montant avec le client avant encaissement\n" +
                   "4. 📝 En cas de problème de paiement, marquez 'EN_COURS' et contactez le contrôleur\n\n" +
                   contextInfo;
        }

        if (containsAny(message, "bonjour", "salut", "hello", "bonsoir")) {
            return "👋 Bonjour! Je suis votre assistant de livraison.\n\n" +
                   "Je peux vous aider avec:\n" +
                   "• 📍 Problèmes d'adresse\n" +
                   "• 👤 Client absent ou qui refuse\n" +
                   "• 📦 Colis endommagé\n" +
                   "• 📅 Report de livraison\n" +
                   "• 💰 Questions de paiement\n" +
                   "• 🆘 Situations urgentes\n\n" +
                   "Décrivez-moi votre situation et je vous guiderai!";
        }

        return "🤖 **Assistant Livraison — Conseils généraux:**\n\n" +
               "Votre message: \"" + request.getMessage() + "\"\n\n" +
               "Voici quelques conseils pour votre tournée:\n" +
               "• 📞 Appelez toujours le client 30 min avant d'arriver\n" +
               "• 📸 Prenez une photo à chaque livraison réussie\n" +
               "• 📝 Notez toute remarque importante dans l'application\n" +
               "• ⏰ Respectez les créneaux de livraison\n" +
               "• 🚗 Optimisez votre trajet en suivant l'ordre proposé\n\n" +
               contextInfo +
               "\n💡 *Pour une aide spécifique, décrivez votre problème avec des mots-clés comme: 'client absent', 'adresse fausse', 'colis endommagé', etc.*";
    }

    /**
     * Get delivery context information for the assistant.
     */
    private String getContextInfo(Long nocde) {
        if (nocde == null) return "";

        LivraisonCom livraison = livraisonRepository.findById(nocde).orElse(null);
        if (livraison == null || livraison.getCommande() == null) return "";

        Commande commande = livraison.getCommande();
        if (commande.getClient() == null) return "";

        StringBuilder context = new StringBuilder();
        context.append("\n📋 **Contexte actuel:**\n");
        context.append("• Commande #: ").append(nocde).append("\n");
        context.append("• Client: ").append(commande.getClient().getNomclt()).append(" ").append(commande.getClient().getPrenomclt()).append("\n");
        context.append("• Adresse: ").append(commande.getClient().getAdrclt()).append(", ").append(commande.getClient().getVilleclt()).append("\n");
        context.append("• Téléphone: ").append(commande.getClient().getTelclt()).append("\n");
        context.append("• Statut: ").append(livraison.getEtatliv()).append("\n");

        return context.toString();
    }

    /**
     * Check if message contains any of the keywords.
     */
    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
