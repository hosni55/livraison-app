package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Personnel entity — represents staff members (livreurs and controleurs).
 * The role is determined by the codeposte field (1=LIVREUR, 2=CONTROLEUR).
 */
@Entity
@Table(name = "Personnel")
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpers")
    private Long idpers;


    @Column(name = "nompers", nullable = false, length = 50)
    private String nompers;

    @Column(name = "prenompers", nullable = false, length = 50)
    private String prenompers;

    @Column(name = "adrpers", length = 200)
    private String adrpers;

    @Column(name = "villepers", length = 50)
    private String villepers;

    @Column(name = "telpers", length = 20)
    private String telpers;

    @Temporal(TemporalType.DATE)
    @Column(name = "d_embauche")
    private Date dEmbauche;

    @Column(name = "Login", nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "motP", nullable = false, length = 100)
    private String motP;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codeposte")
    private Postes poste;

    // Getters and Setters
    public Long getIdpers() { return idpers; }
    public void setIdpers(Long idpers) { this.idpers = idpers; }

    public String getNompers() { return nompers; }
    public void setNompers(String nompers) { this.nompers = nompers; }

    public String getPrenompers() { return prenompers; }
    public void setPrenompers(String prenompers) { this.prenompers = prenompers; }

    public String getAdrpers() { return adrpers; }
    public void setAdrpers(String adrpers) { this.adrpers = adrpers; }

    public String getVillepers() { return villepers; }
    public void setVillepers(String villepers) { this.villepers = villepers; }

    public String getTelpers() { return telpers; }
    public void setTelpers(String telpers) { this.telpers = telpers; }

    public Date getDEmbauche() { return dEmbauche; }
    public void setDEmbauche(Date dEmbauche) { this.dEmbauche = dEmbauche; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMotP() { return motP; }
    public void setMotP(String motP) { this.motP = motP; }

    public Postes getPoste() { return poste; }
    public void setPoste(Postes poste) { this.poste = poste; }

    /**
     * Returns the role string based on poste code.
     * codeposte 1 = LIVREUR, 2 = CONTROLEUR
     */
    public String getRole() {
        if (poste != null && poste.getCodeposte() != null) {
            return poste.getCodeposte() == 1 ? "LIVREUR" : "CONTROLEUR";
        }
        return "UNKNOWN";
    }
}
