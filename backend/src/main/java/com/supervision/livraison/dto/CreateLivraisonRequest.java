package com.supervision.livraison.dto;

import java.util.Date;

/**
 * Request DTO for creating a new delivery.
 */
public class CreateLivraisonRequest {
    private Long clientId;
    private Long livreurId;
    private Double montant;
    private String modePaiement;
    private Date dateLivraison;

    public CreateLivraisonRequest() {
        this.dateLivraison = new Date(); // Default to today
        this.modePaiement = "ESPECE";
    }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getLivreurId() { return livreurId; }
    public void setLivreurId(Long livreurId) { this.livreurId = livreurId; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public Date getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(Date dateLivraison) { this.dateLivraison = dateLivraison; }
}
