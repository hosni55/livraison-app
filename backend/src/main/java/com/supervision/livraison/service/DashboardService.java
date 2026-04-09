package com.supervision.livraison.service;

import com.supervision.livraison.dto.DashboardStats;
import com.supervision.livraison.repository.LivraisonRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Dashboard service — provides analytics and statistics for the controleur dashboard.
 */
@Service
public class DashboardService {

    private final LivraisonRepository livraisonRepository;

    public DashboardService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
    }

    /**
     * Get overall dashboard statistics.
     */
    public DashboardStats getStats() {
        List<Object[]> statusCounts = livraisonRepository.countByStatus();

        DashboardStats stats = new DashboardStats();
        long total = 0;
        long livrees = 0;
        long enCours = 0;
        long planifiees = 0;
        long echecs = 0;
        long retardees = 0;

        for (Object[] row : statusCounts) {
            String etat = (String) row[0];
            Long count = (Long) row[1];
            total += count;

            switch (etat) {
                case "LIVREE" -> livrees = count;
                case "EN_COURS" -> enCours = count;
                case "PLANIFIEE" -> planifiees = count;
                case "ECHEC" -> echecs = count;
                case "RETARDEE" -> retardees = count;
            }
        }

        stats.setTotalLivraisons(total);
        stats.setLivrees(livrees);
        stats.setEnCours(enCours);
        stats.setPlanifiees(planifiees);
        stats.setEchecs(echecs);
        stats.setRetardees(retardees);
        stats.setTauxReussite(total > 0 ? (double) livrees / total * 100 : 0.0);
        stats.setDate(new Date());

        return stats;
    }

    /**
     * Get delivery counts per livreur.
     * Returns a list of maps with livreur info and counts.
     */
    public List<Map<String, Object>> getByLivreur() {
        List<Object[]> results = livraisonRepository.countByLivreur();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("livreurId", row[0]);
            map.put("nom", row[1] + " " + row[2]);
            map.put("total", row[3]);
            map.put("livrees", row[4]);
            map.put("echecs", row[5]);
            data.add(map);
        }

        return data;
    }

    /**
     * Get delivery counts per client city.
     */
    public List<Map<String, Object>> getByClient() {
        List<Object[]> results = livraisonRepository.countByClientVille();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("ville", row[0]);
            map.put("total", row[1]);
            data.add(map);
        }

        return data;
    }
}
