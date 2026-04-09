package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Client entity.
 */
@Entity
@Table(name = "Clients")
public class Client {

    @Id
    @Column(name = "noclt")
    private Long noclt;

    @Column(name = "nomclt", nullable = false, length = 50)
    private String nomclt;

    @Column(name = "prenomclt", length = 50)
    private String prenomclt;

    @Column(name = "adrclt", nullable = false, length = 200)
    private String adrclt;

    @Column(name = "villeclt", length = 50)
    private String villeclt;

    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Column(name = "telclt", length = 20)
    private String telclt;

    @Column(name = "adrmail", length = 100)
    private String adrmail;

    public Long getNoclt() { return noclt; }
    public void setNoclt(Long noclt) { this.noclt = noclt; }

    public String getNomclt() { return nomclt; }
    public void setNomclt(String nomclt) { this.nomclt = nomclt; }

    public String getPrenomclt() { return prenomclt; }
    public void setPrenomclt(String prenomclt) { this.prenomclt = prenomclt; }

    public String getAdrclt() { return adrclt; }
    public void setAdrclt(String adrclt) { this.adrclt = adrclt; }

    public String getVilleclt() { return villeclt; }
    public void setVilleclt(String villeclt) { this.villeclt = villeclt; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getTelclt() { return telclt; }
    public void setTelclt(String telclt) { this.telclt = telclt; }

    public String getAdrmail() { return adrmail; }
    public void setAdrmail(String adrmail) { this.adrmail = adrmail; }
}
