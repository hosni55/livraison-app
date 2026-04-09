package com.supervision.livraison.service;

import com.supervision.livraison.dto.CreateLivraisonRequest;
import com.supervision.livraison.dto.LivraisonDTO;
import com.supervision.livraison.dto.StatusUpdateRequest;
import com.supervision.livraison.entity.*;
import com.supervision.livraison.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Livraison service — handles delivery CRUD operations and status updates.
 * Sends WebSocket notifications on status changes.
 */
@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeRepository commandeRepository;
    private final PersonnelRepository personnelRepository;
    private final ClientRepository clientRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public LivraisonService(LivraisonRepository livraisonRepository,
                            CommandeRepository commandeRepository,
                            PersonnelRepository personnelRepository,
                            ClientRepository clientRepository,
                            SimpMessagingTemplate messagingTemplate) {
        this.livraisonRepository = livraisonRepository;
        this.commandeRepository = commandeRepository;
        this.personnelRepository = personnelRepository;
        this.clientRepository = clientRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Get all deliveries.
     */
    public List<LivraisonDTO> getAllLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get today's deliveries.
     */
    public List<LivraisonDTO> getTodayDeliveries() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.DATE, 1);

        return livraisonRepository.findTodayDeliveries(startCal.getTime(), endCal.getTime()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get deliveries for a specific livreur.
     */
    public List<LivraisonDTO> getLivraisonsByLivreur(Long livreurId) {
        return livraisonRepository.findByLivreurId(livreurId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a single delivery by ID.
     */
    public LivraisonDTO getLivraisonById(Long nocde) {
        LivraisonCom livraison = livraisonRepository.findById(nocde)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée: " + nocde));
        return toDTO(livraison);
    }

    /**
     * Update delivery status with optional remarque.
     * Sends WebSocket notification to controleur on status change.
     */
    @Transactional
    public LivraisonDTO updateStatus(Long nocde, StatusUpdateRequest request) {
        LivraisonCom livraison = livraisonRepository.findById(nocde)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée: " + nocde));

        String oldStatus = livraison.getEtatliv();
        livraison.setEtatliv(request.getEtatliv());

        LivraisonCom saved = livraisonRepository.save(livraison);
        LivraisonDTO dto = toDTO(saved);

        // Send WebSocket notification about status change
        messagingTemplate.convertAndSend("/topic/livraison-status", dto);

        // If status changed to ECHEC, send urgent notification to controleurs
        if ("ECHEC".equals(request.getEtatliv())) {
            messagingTemplate.convertAndSend("/topic/alerts",
                    "Échec livraison #" + nocde + " — " + request.getRemarque());
        }

        return dto;
    }

    /**
     * Update delivery remarque (notes/comments).
     */
    @Transactional
    public LivraisonDTO updateRemarque(Long nocde, String remarque) {
        LivraisonCom livraison = livraisonRepository.findById(nocde)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée: " + nocde));
        // In a real app, remarque would be stored in a separate table
        // For now, we return the DTO (remarque field is in DTO only)
        return toDTO(livraison);
    }

    /**
     * Create a new delivery (and the associated order).
     * Atomic transaction to ensure consistency.
     */
    @Transactional
    public LivraisonDTO createLivraison(CreateLivraisonRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Personnel driver = personnelRepository.findById(request.getLivreurId())
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));

        // 1. Create Commande
        Commande commande = new Commande();
        // Simple incremental fake ID for nocde (In reality, use DB sequence)
        long newNocde = System.currentTimeMillis() % 10000000;
        commande.setNocde(newNocde);
        commande.setClient(client);
        commande.setDatecde(new Date());
        commande.setEtatcde("LIVRAISON");
        commandeRepository.save(commande);

        // 2. Create Livraison
        LivraisonCom livraison = new LivraisonCom();
        livraison.setNocde(newNocde);
        livraison.setCommande(commande);
        livraison.setDateliv(request.getDateLivraison());
        livraison.setLivreur(driver);
        livraison.setModepay(request.getModePaiement());
        livraison.setEtatliv("PLANIFIEE");

        LivraisonCom saved = livraisonRepository.save(livraison);
        LivraisonDTO dto = toDTO(saved);

        // Notify app
        messagingTemplate.convertAndSend("/topic/livraison-status", dto);

        return dto;
    }

    /**
     * Convert LivraisonCom entity to DTO.
     */
    private LivraisonDTO toDTO(LivraisonCom l) {
        LivraisonDTO dto = new LivraisonDTO();
        dto.setNocde(l.getNocde());
        dto.setDateliv(l.getDateliv());
        dto.setEtatliv(l.getEtatliv());
        dto.setModepay(l.getModepay());

        if (l.getLivreur() != null) {
            dto.setLivreurId(l.getLivreur().getIdpers());
            dto.setLivreurNom(l.getLivreur().getNompers() + " " + l.getLivreur().getPrenompers());
            dto.setLivreurTel(l.getLivreur().getTelpers());
        }

        if (l.getCommande() != null && l.getCommande().getClient() != null) {
            Client client = l.getCommande().getClient();
            dto.setClientId(client.getNoclt());
            dto.setClientNom(client.getNomclt() + " " + client.getPrenomclt());
            dto.setClientAdresse(client.getAdrclt());
            dto.setClientVille(client.getVilleclt());
            dto.setClientTel(client.getTelclt());
            dto.setDateCommande(l.getCommande().getDatecde());
        }

        return dto;
    }
}
