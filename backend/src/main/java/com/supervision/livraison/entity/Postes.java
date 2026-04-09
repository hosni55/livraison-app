package com.supervision.livraison.entity;

import jakarta.persistence.*;

/**
 * Postes entity — job positions for personnel.
 */
@Entity
@Table(name = "Postes")
public class Postes {

    @Id
    @Column(name = "codeposte")
    private Long codeposte;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "indice")
    private Integer indice;

    public Long getCodeposte() { return codeposte; }
    public void setCodeposte(Long codeposte) { this.codeposte = codeposte; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Integer getIndice() { return indice; }
    public void setIndice(Integer indice) { this.indice = indice; }
}
