package com.supervision.livraison.model;

/**
 * Dashboard statistics model.
 */
public class DashboardStats {
    private Long totalLivraisons;
    private Long livrees;
    private Long enCours;
    private Long planifiees;
    private Long echecs;
    private Long retardees;
    private Double tauxReussite;

    public Long getTotalLivraisons() { return totalLivraisons; }
    public Long getLivrees() { return livrees; }
    public Long getEnCours() { return enCours; }
    public Long getPlanifiees() { return planifiees; }
    public Long getEchecs() { return echecs; }
    public Long getRetardees() { return retardees; }
    public Double getTauxReussite() { return tauxReussite; }
}
