package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Article entity — products/items that can be ordered.
 */
@Entity
@Table(name = "Articles")
public class Article {

    @Id
    @Column(name = "refart", length = 20)
    private String refart;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Column(name = "prixA", precision = 10, scale = 2)
    private BigDecimal prixA;

    @Column(name = "prixV", precision = 10, scale = 2)
    private BigDecimal prixV;

    @Column(name = "codetva", precision = 5, scale = 2)
    private BigDecimal codetva;

    @Column(name = "categorie", length = 50)
    private String categorie;

    @Column(name = "qtestk")
    private Integer qtestk;

    public String getRefart() { return refart; }
    public void setRefart(String refart) { this.refart = refart; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public BigDecimal getPrixA() { return prixA; }
    public void setPrixA(BigDecimal prixA) { this.prixA = prixA; }

    public BigDecimal getPrixV() { return prixV; }
    public void setPrixV(BigDecimal prixV) { this.prixV = prixV; }

    public BigDecimal getCodetva() { return codetva; }
    public void setCodetva(BigDecimal codetva) { this.codetva = codetva; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public Integer getQtestk() { return qtestk; }
    public void setQtestk(Integer qtestk) { this.qtestk = qtestk; }
}
