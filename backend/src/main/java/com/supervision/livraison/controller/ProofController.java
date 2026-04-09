package com.supervision.livraison.controller;

import com.supervision.livraison.entity.Commande;
import com.supervision.livraison.entity.DeliveryProof;
import com.supervision.livraison.repository.CommandeRepository;
import com.supervision.livraison.repository.DeliveryProofRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Proof controller — handles delivery proof uploads (photos and signatures).
 */
@RestController
@RequestMapping("/api/proof")
@CrossOrigin(origins = "*")
public class ProofController {

    private final DeliveryProofRepository proofRepository;
    private final CommandeRepository commandeRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public ProofController(DeliveryProofRepository proofRepository, CommandeRepository commandeRepository) {
        this.proofRepository = proofRepository;
        this.commandeRepository = commandeRepository;
    }

    /**
     * POST /api/proof/upload — Upload delivery proof (photo + signature).
     * Accepts multipart file upload with commande ID.
     */
    @PostMapping("/upload")
    public ResponseEntity<DeliveryProof> uploadProof(
            @RequestParam("nocde") Long nocde,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "signature", required = false) MultipartFile signature) throws IOException {

        Commande commande = commandeRepository.findById(nocde)
                .orElseThrow(() -> new RuntimeException("Commande not found: " + nocde));

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        DeliveryProof proof = new DeliveryProof();
        proof.setCommande(commande);

        // Save photo if provided
        if (photo != null && !photo.isEmpty()) {
            String photoFileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            Path photoPath = uploadPath.resolve(photoFileName);
            Files.copy(photo.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);
            proof.setPhotoPath(photoPath.toString());
        }

        // Save signature if provided
        if (signature != null && !signature.isEmpty()) {
            String sigFileName = UUID.randomUUID() + "_" + signature.getOriginalFilename();
            Path sigPath = uploadPath.resolve(sigFileName);
            Files.copy(signature.getInputStream(), sigPath, StandardCopyOption.REPLACE_EXISTING);
            proof.setSignaturePath(sigPath.toString());
        }

        DeliveryProof saved = proofRepository.save(proof);
        return ResponseEntity.ok(saved);
    }

    /**
     * GET /api/proof/{nocde} — Get all proofs for a delivery.
     */
    @GetMapping("/{nocde}")
    public ResponseEntity<?> getProofsByCommande(@PathVariable Long nocde) {
        return ResponseEntity.ok(proofRepository.findByCommandeNocde(nocde));
    }
}
