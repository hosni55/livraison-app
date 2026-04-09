package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * LivraisonCom entity — delivery records linked to orders.
 */
@Entity
@Table(name = "LivraisonCom")
public class LivraisonCom {

    @Id
    @Column(name = "nocde")
    private Long nocde;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "nocde")
    private Commande commande;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dateliv")
    private Date dateliv;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livreur")
    private Personnel livreur;

    @Column(name = "modepay", length = 30)
    private String modepay;

    @Column(name = "etatliv", length = 30)
    private String etatliv;

    public Long getNocde() { return nocde; }
    public void setNocde(Long nocde) { this.nocde = nocde; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Date getDateliv() { return dateliv; }
    public void setDateliv(Date dateliv) { this.dateliv = dateliv; }

    public Personnel getLivreur() { return livreur; }
    public void setLivreur(Personnel livreur) { this.livreur = livreur; }

    public String getModepay() { return modepay; }
    public void setModepay(String modepay) { this.modepay = modepay; }

    public String getEtatliv() { return etatliv; }
    public void setEtatliv(String etatliv) { this.etatliv = etatliv; }
}
