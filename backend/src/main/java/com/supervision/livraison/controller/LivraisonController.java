package com.supervision.livraison.controller;

import com.supervision.livraison.dto.CreateLivraisonRequest;
import com.supervision.livraison.dto.LivraisonDTO;
import com.supervision.livraison.dto.StatusUpdateRequest;
import com.supervision.livraison.service.LivraisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Livraison controller — manages delivery operations.
 */
@RestController
@RequestMapping("/api/livraisons")
@CrossOrigin(origins = "*")
public class LivraisonController {

    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    /**
     * GET /api/livraisons — Get all deliveries (controleur only).
     */
    @GetMapping
    public ResponseEntity<List<LivraisonDTO>> getAllLivraisons() {
        return ResponseEntity.ok(livraisonService.getAllLivraisons());
    }

    /**
     * GET /api/livraisons/today — Get today's deliveries.
     */
    @GetMapping("/today")
    public ResponseEntity<List<LivraisonDTO>> getTodayDeliveries() {
        return ResponseEntity.ok(livraisonService.getTodayDeliveries());
    }

    /**
     * GET /api/livraisons/{id} — Get a single delivery.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> getLivraisonById(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.getLivraisonById(id));
    }

    /**
     * PUT /api/livraisons/{id}/status — Update delivery status.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<LivraisonDTO> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(livraisonService.updateStatus(id, request));
    }

    /**
     * PUT /api/livraisons/{id}/remarque — Update delivery remarks.
     */
    @PutMapping("/{id}/remarque")
    public ResponseEntity<LivraisonDTO> updateRemarque(@PathVariable Long id, @RequestBody String remarque) {
        return ResponseEntity.ok(livraisonService.updateRemarque(id, remarque));
    }

    /**
     * POST /api/livraisons — Create a new delivery.
     */
    @PostMapping
    public ResponseEntity<LivraisonDTO> createLivraison(@RequestBody CreateLivraisonRequest request) {
        return ResponseEntity.ok(livraisonService.createLivraison(request));
    }
}
