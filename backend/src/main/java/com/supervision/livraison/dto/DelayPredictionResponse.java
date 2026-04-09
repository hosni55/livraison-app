package com.supervision.livraison.dto;

/**
 * AI delay prediction response DTO.
 */
public class DelayPredictionResponse {
    private String prediction;
    private Double confidence;
    private String message;

    public DelayPredictionResponse() {}

    public DelayPredictionResponse(String prediction, Double confidence, String message) {
        this.prediction = prediction;
        this.confidence = confidence;
        this.message = message;
    }

    public String getPrediction() { return prediction; }
    public void setPrediction(String prediction) { this.prediction = prediction; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
