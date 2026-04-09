package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * LigCde entity — order line items (composite key: nocde + refart).
 */
@Entity
@Table(name = "LigCdes")
@IdClass(LigCde.LigCdeId.class)
public class LigCde {

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nocde", nullable = false)
    private Commande commande;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refart", nullable = false)
    private Article article;

    @Column(name = "qtecde", nullable = false)
    private Integer qtecde;

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public Integer getQtecde() { return qtecde; }
    public void setQtecde(Integer qtecde) { this.qtecde = qtecde; }

    /**
     * Composite key class for LigCde.
     */
    public static class LigCdeId implements Serializable {
        private Long commande;
        private String article;

        public LigCdeId() {}

        public LigCdeId(Long commande, String article) {
            this.commande = commande;
            this.article = article;
        }

        public Long getCommande() { return commande; }
        public void setCommande(Long commande) { this.commande = commande; }

        public String getArticle() { return article; }
        public void setArticle(String article) { this.article = article; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LigCdeId that = (LigCdeId) o;
            if (commande == null || article == null) return false;
            return commande.equals(that.commande) && article.equals(that.article);
        }

        @Override
        public int hashCode() {
            int result = commande != null ? commande.hashCode() : 0;
            result = 31 * result + (article != null ? article.hashCode() : 0);
            return result;
        }
    }
}
