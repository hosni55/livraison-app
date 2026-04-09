package com.supervision.livraison.controller;

import com.supervision.livraison.dto.DashboardStats;
import com.supervision.livraison.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dashboard controller — provides analytics for the controleur dashboard.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /api/dashboard/stats — Get overall dashboard statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    /**
     * GET /api/dashboard/by-livreur — Get delivery counts per livreur.
     */
    @GetMapping("/by-livreur")
    public ResponseEntity<List<Map<String, Object>>> getByLivreur() {
        return ResponseEntity.ok(dashboardService.getByLivreur());
    }

    /**
     * GET /api/dashboard/by-client — Get delivery counts per client city.
     */
    @GetMapping("/by-client")
    public ResponseEntity<List<Map<String, Object>>> getByClient() {
        return ResponseEntity.ok(dashboardService.getByClient());
    }
}
