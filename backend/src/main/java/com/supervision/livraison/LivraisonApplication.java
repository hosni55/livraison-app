package com.supervision.livraison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point.
 * Enables scheduling for periodic AI model retraining.
 */
@SpringBootApplication
@EnableScheduling
public class LivraisonApplication {
    public static void main(String[] args) {
        SpringApplication.run(LivraisonApplication.class, args);
    }
}
