package com.supervision.livraison.dto;

import java.util.Date;

/**
 * Livraison DTO — delivery information for API responses.
 */
public class LivraisonDTO {
    private Long nocde;
    private Date dateliv;
    private String etatliv;
    private String modepay;
    private Long livreurId;
    private String livreurNom;
    private String livreurTel;
    private Long clientId;
    private String clientNom;
    private String clientAdresse;
    private String clientVille;
    private String clientTel;
    private Date dateCommande;
    private Integer nbArticles;
    private Integer totalQuantite;
    private Double montantTotal;
    private String remarque;

    public LivraisonDTO() {}

    public Long getNocde() { return nocde; }
    public void setNocde(Long nocde) { this.nocde = nocde; }

    public Date getDateliv() { return dateliv; }
    public void setDateliv(Date dateliv) { this.dateliv = dateliv; }

    public String getEtatliv() { return etatliv; }
    public void setEtatliv(String etatliv) { this.etatliv = etatliv; }

    public String getModepay() { return modepay; }
    public void setModepay(String modepay) { this.modepay = modepay; }

    public Long getLivreurId() { return livreurId; }
    public void setLivreurId(Long livreurId) { this.livreurId = livreurId; }

    public String getLivreurNom() { return livreurNom; }
    public void setLivreurNom(String livreurNom) { this.livreurNom = livreurNom; }

    public String getLivreurTel() { return livreurTel; }
    public void setLivreurTel(String livreurTel) { this.livreurTel = livreurTel; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public String getClientAdresse() { return clientAdresse; }
    public void setClientAdresse(String clientAdresse) { this.clientAdresse = clientAdresse; }

    public String getClientVille() { return clientVille; }
    public void setClientVille(String clientVille) { this.clientVille = clientVille; }

    public String getClientTel() { return clientTel; }
    public void setClientTel(String clientTel) { this.clientTel = clientTel; }

    public Date getDateCommande() { return dateCommande; }
    public void setDateCommande(Date dateCommande) { this.dateCommande = dateCommande; }

    public Integer getNbArticles() { return nbArticles; }
    public void setNbArticles(Integer nbArticles) { this.nbArticles = nbArticles; }

    public Integer getTotalQuantite() { return totalQuantite; }
    public void setTotalQuantite(Integer totalQuantite) { this.totalQuantite = totalQuantite; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
