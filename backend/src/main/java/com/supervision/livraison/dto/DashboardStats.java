package com.supervision.livraison.dto;

import java.util.Date;

/**
 * Dashboard statistics DTO.
 */
public class DashboardStats {
    private Long totalLivraisons;
    private Long livrees;
    private Long enCours;
    private Long planifiees;
    private Long echecs;
    private Long retardees;
    private Double tauxReussite;
    private Date date;

    public DashboardStats() {}

    public Long getTotalLivraisons() { return totalLivraisons; }
    public void setTotalLivraisons(Long totalLivraisons) { this.totalLivraisons = totalLivraisons; }

    public Long getLivrees() { return livrees; }
    public void setLivrees(Long livrees) { this.livrees = livrees; }

    public Long getEnCours() { return enCours; }
    public void setEnCours(Long enCours) { this.enCours = enCours; }

    public Long getPlanifiees() { return planifiees; }
    public void setPlanifiees(Long planifiees) { this.planifiees = planifiees; }

    public Long getEchecs() { return echecs; }
    public void setEchecs(Long echecs) { this.echecs = echecs; }

    public Long getRetardees() { return retardees; }
    public void setRetardees(Long retardees) { this.retardees = retardees; }

    public Double getTauxReussite() { return tauxReussite; }
    public void setTauxReussite(Double tauxReussite) { this.tauxReussite = tauxReussite; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}
