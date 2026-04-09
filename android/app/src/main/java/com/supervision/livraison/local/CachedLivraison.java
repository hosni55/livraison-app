package com.supervision.livraison.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity for caching deliveries locally for offline access.
 */
@Entity(tableName = "cached_livraisons")
public class CachedLivraison {

    @PrimaryKey
    private Long nocde;

    private String clientNom;
    private String clientAdresse;
    private String clientVille;
    private String clientTel;
    private String livreurNom;
    private String etatliv;
    private String modepay;
    private String dateliv;
    private Integer nbArticles;
    private Double montantTotal;
    private String remarque;

    public Long getNocde() { return nocde; }
    public void setNocde(Long nocde) { this.nocde = nocde; }

    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public String getClientAdresse() { return clientAdresse; }
    public void setClientAdresse(String clientAdresse) { this.clientAdresse = clientAdresse; }

    public String getClientVille() { return clientVille; }
    public void setClientVille(String clientVille) { this.clientVille = clientVille; }

    public String getClientTel() { return clientTel; }
    public void setClientTel(String clientTel) { this.clientTel = clientTel; }

    public String getLivreurNom() { return livreurNom; }
    public void setLivreurNom(String livreurNom) { this.livreurNom = livreurNom; }

    public String getEtatliv() { return etatliv; }
    public void setEtatliv(String etatliv) { this.etatliv = etatliv; }

    public String getModepay() { return modepay; }
    public void setModepay(String modepay) { this.modepay = modepay; }

    public String getDateliv() { return dateliv; }
    public void setDateliv(String dateliv) { this.dateliv = dateliv; }

    public Integer getNbArticles() { return nbArticles; }
    public void setNbArticles(Integer nbArticles) { this.nbArticles = nbArticles; }

    public Double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Double montantTotal) { this.montantTotal = montantTotal; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
