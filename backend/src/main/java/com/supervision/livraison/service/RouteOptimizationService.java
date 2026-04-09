package com.supervision.livraison.service;

import com.supervision.livraison.dto.LivraisonDTO;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Route optimization service — sorts deliveries by shortest path
 * using a greedy nearest-neighbor algorithm on GPS coordinates.
 *
 * Since we don't have actual GPS coordinates for clients in the database,
 * this uses simulated coordinates based on city names for demonstration.
 * In production, integrate with Google Maps Distance Matrix API.
 */
@Service
public class RouteOptimizationService {

    // Simulated GPS coordinates for Tunisian cities (for demonstration)
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();
    static {
        CITY_COORDS.put("Tunis", new double[]{36.8065, 10.1815});
        CITY_COORDS.put("Sfax", new double[]{34.7406, 10.7603});
        CITY_COORDS.put("Sousse", new double[]{35.8256, 10.6369});
        CITY_COORDS.put("Ariana", new double[]{36.8625, 10.1956});
        CITY_COORDS.put("Nabeul", new double[]{36.4561, 10.7376});
        CITY_COORDS.put("Bizerte", new double[]{37.2744, 9.8739});
        CITY_COORDS.put("Kairouan", new double[]{35.6781, 10.0963});
        CITY_COORDS.put("Monastir", new double[]{35.7774, 10.8264});
        CITY_COORDS.put("La Marsa", new double[]{36.8785, 10.3247});
        CITY_COORDS.put("Sidi Bouzid", new double[]{35.0381, 9.4858});
    }

    /**
     * Sort deliveries by nearest-neighbor algorithm.
     * Starts from the livreur's current position and visits closest delivery next.
     */
    public List<LivraisonDTO> optimizeRoute(List<LivraisonDTO> deliveries, double startLat, double startLng) {
        if (deliveries == null || deliveries.isEmpty()) {
            return Collections.emptyList();
        }

        List<LivraisonDTO> optimized = new ArrayList<>();
        List<LivraisonDTO> remaining = new ArrayList<>(deliveries);

        double currentLat = startLat;
        double currentLng = startLng;

        while (!remaining.isEmpty()) {
            int nearestIndex = 0;
            double nearestDistance = Double.MAX_VALUE;

            for (int i = 0; i < remaining.size(); i++) {
                LivraisonDTO delivery = remaining.get(i);
                double[] coords = getCoordinates(delivery);
                double distance = haversineDistance(currentLat, currentLng, coords[0], coords[1]);

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestIndex = i;
                }
            }

            LivraisonDTO nearest = remaining.remove(nearestIndex);
            optimized.add(nearest);

            double[] coords = getCoordinates(nearest);
            currentLat = coords[0];
            currentLng = coords[1];
        }

        return optimized;
    }

    /**
     * Get GPS coordinates for a delivery based on client city.
     */
    private double[] getCoordinates(LivraisonDTO delivery) {
        String ville = delivery.getClientVille();
        if (ville != null && CITY_COORDS.containsKey(ville)) {
            return CITY_COORDS.get(ville);
        }
        // Default to Tunis if city not found
        return CITY_COORDS.get("Tunis");
    }

    /**
     * Calculate distance between two GPS coordinates using Haversine formula.
     * Returns distance in kilometers.
     */
    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0; // Earth's radius in km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
