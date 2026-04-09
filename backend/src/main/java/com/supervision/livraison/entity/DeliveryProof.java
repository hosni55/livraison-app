package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * DeliveryProof entity — photo and signature proofs for deliveries.
 */
@Entity
@Table(name = "DELIVERY_PROOFS")
public class DeliveryProof {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proof_seq")
    @SequenceGenerator(name = "proof_seq", sequenceName = "SEQ_DELIVERY_PROOFS", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nocde", nullable = false)
    private Commande commande;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "signature_path", length = 500)
    private String signaturePath;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getSignaturePath() { return signaturePath; }
    public void setSignaturePath(String signaturePath) { this.signaturePath = signaturePath; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
