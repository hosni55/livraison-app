package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Commande entity — customer orders.
 */
@Entity
@Table(name = "Commandes")
public class Commande {

    @Id
    @Column(name = "nocde")
    private Long nocde;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "noclt", nullable = false)
    private Client client;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datecde", nullable = false)
    private Date datecde;

    @Column(name = "etatcde", length = 20)
    private String etatcde;

    @OneToMany(mappedBy = "commande", fetch = FetchType.LAZY)
    private List<LigCde> lignes;

    @OneToOne(mappedBy = "commande", fetch = FetchType.LAZY)
    private LivraisonCom livraison;

    public Long getNocde() { return nocde; }
    public void setNocde(Long nocde) { this.nocde = nocde; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Date getDatecde() { return datecde; }
    public void setDatecde(Date datecde) { this.datecde = datecde; }

    public String getEtatcde() { return etatcde; }
    public void setEtatcde(String etatcde) { this.etatcde = etatcde; }

    public List<LigCde> getLignes() { return lignes; }
    public void setLignes(List<LigCde> lignes) { this.lignes = lignes; }

    public LivraisonCom getLivraison() { return livraison; }
    public void setLivraison(LivraisonCom livraison) { this.livraison = livraison; }
}
