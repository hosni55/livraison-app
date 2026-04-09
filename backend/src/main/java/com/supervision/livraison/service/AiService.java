package com.supervision.livraison.service;

import com.supervision.livraison.dto.DelayPredictionRequest;
import com.supervision.livraison.dto.DelayPredictionResponse;
import com.supervision.livraison.entity.LivraisonCom;
import com.supervision.livraison.repository.LivraisonRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * AI service — handles delivery delay prediction using Weka RandomForest.
 * Features: hour_of_day, day_of_week, zone, livreur_id, nb_articles, distance_km
 * Labels: ON_TIME, DELAYED, FAILED
 *
 * Auto-retrains weekly using @Scheduled annotation.
 */
@Service
public class AiService {

    private final LivraisonRepository livraisonRepository;
    private Classifier model;
    private Instances datasetStructure;
    private boolean modelTrained = false;

    public AiService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
        initializeDatasetStructure();
    }

    /**
     * Define the dataset structure with all features and class attribute.
     */
    private void initializeDatasetStructure() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("hour_of_day"));
        attributes.add(new Attribute("day_of_week"));

        // Zone (ville) as nominal attribute
        ArrayList<String> zoneValues = new ArrayList<>();
        zoneValues.add("Tunis");
        zoneValues.add("Sfax");
        zoneValues.add("Sousse");
        zoneValues.add("Ariana");
        zoneValues.add("Nabeul");
        zoneValues.add("Bizerte");
        zoneValues.add("Kairouan");
        zoneValues.add("Monastir");
        zoneValues.add("La_Marsa");
        zoneValues.add("Sidi_Bouzid");
        attributes.add(new Attribute("zone", zoneValues));

        attributes.add(new Attribute("livreur_id"));
        attributes.add(new Attribute("nb_articles"));
        attributes.add(new Attribute("distance_km"));

        // Class attribute (prediction label)
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("ON_TIME");
        classValues.add("DELAYED");
        classValues.add("FAILED");
        attributes.add(new Attribute("class", classValues));

        datasetStructure = new Instances("DeliveryDelayPrediction", attributes, 0);
        datasetStructure.setClassIndex(datasetStructure.numAttributes() - 1);
    }

    /**
     * Predict delivery delay based on input features.
     */
    public DelayPredictionResponse predictDelay(DelayPredictionRequest request) {
        if (!modelTrained) {
            trainModel();
        }

        try {
            DenseInstance instance = new DenseInstance(datasetStructure.numAttributes());
            instance.setDataset(datasetStructure);

            instance.setValue(0, request.getHourOfDay() != null ? request.getHourOfDay() : 10);
            instance.setValue(1, request.getDayOfWeek() != null ? request.getDayOfWeek() : 1);
            instance.setValue(2, request.getZone() != null ? request.getZone() : "Tunis");
            instance.setValue(3, request.getLivreurId() != null ? request.getLivreurId() : 1);
            instance.setValue(4, request.getNbArticles() != null ? request.getNbArticles() : 1);
            instance.setValue(5, request.getDistanceKm() != null ? request.getDistanceKm() : 5.0);

            double prediction = model.classifyInstance(instance);
            String label = datasetStructure.classAttribute().value((int) prediction);

            // Get confidence from distribution
            double[] distribution = model.distributionForInstance(instance);
            double confidence = distribution[(int) prediction] * 100;

            String message = generateMessage(label, confidence);

            return new DelayPredictionResponse(label, Math.round(confidence * 100.0) / 100.0, message);

        } catch (Exception e) {
            return new DelayPredictionResponse("UNKNOWN", 0.0, "Erreur de prédiction: " + e.getMessage());
        }
    }

    /**
     * Train the RandomForest model using historical delivery data.
     * Maps delivery statuses to prediction labels:
     * - LIVREE → ON_TIME
     * - RETARDEE → DELAYED
     * - ECHEC → FAILED
     */
    @Scheduled(cron = "0 0 2 * * 0") // Every Sunday at 2 AM
    public synchronized void trainModel() {
        try {
            List<LivraisonCom> deliveries = livraisonRepository.findAll();
            Instances trainingData = new Instances(datasetStructure);

            for (LivraisonCom delivery : deliveries) {
                DenseInstance instance = new DenseInstance(datasetStructure.numAttributes());
                instance.setDataset(trainingData);

                Date dateLiv = delivery.getDateliv();
                if (dateLiv == null) continue;

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(dateLiv);

                instance.setValue(0, cal.get(java.util.Calendar.HOUR_OF_DAY));
                instance.setValue(1, cal.get(java.util.Calendar.DAY_OF_WEEK));

                String zone = "Tunis";
                if (delivery.getCommande() != null && delivery.getCommande().getClient() != null) {
                    String ville = delivery.getCommande().getClient().getVilleclt();
                    if (ville != null) {
                        zone = ville.replace(" ", "_");
                    }
                }
                instance.setValue(2, zone);

                if (delivery.getLivreur() != null) {
                    instance.setValue(3, delivery.getLivreur().getIdpers());
                }

                // Simulate nb_articles and distance (in production, calculate from actual data)
                instance.setValue(4, 2);
                instance.setValue(5, 10.0);

                // Map etatliv to class label
                String label = mapStatusToLabel(delivery.getEtatliv());
                instance.setClassValue(label);

                trainingData.add(instance);
            }

            if (trainingData.numInstances() > 10) {
                RandomForest rf = new RandomForest();
                rf.setNumIterations(100);
                rf.buildClassifier(trainingData);

                model = rf;
                modelTrained = true;
                System.out.println("Weka RandomForest model trained with " + trainingData.numInstances() + " instances.");
            } else {
                System.out.println("Not enough training data. Need at least 10 instances.");
            }
        } catch (Exception e) {
            System.err.println("Error training model: " + e.getMessage());
        }
    }

    /**
     * Map delivery status to prediction label.
     */
    private String mapStatusToLabel(String etatliv) {
        if (etatliv == null) return "ON_TIME";
        return switch (etatliv) {
            case "LIVREE" -> "ON_TIME";
            case "RETARDEE" -> "DELAYED";
            case "ECHEC" -> "FAILED";
            default -> "ON_TIME";
        };
    }

    /**
     * Generate a human-readable message based on prediction.
     */
    private String generateMessage(String label, double confidence) {
        return switch (label) {
            case "ON_TIME" -> String.format("Livraison à l'heure prévue (confiance: %.1f%%)", confidence);
            case "DELAYED" -> String.format("Risque de retard détecté (confiance: %.1f%%). Prévoyez un départ anticipé.", confidence);
            case "FAILED" -> String.format("Risque d'échec élevé (confiance: %.1f%%). Contactez le client avant de vous déplacer.", confidence);
            default -> "Prédiction non disponible.";
        };
    }
}
