package com.supervision.livraison.dto;

/**
 * AI delay prediction request DTO.
 */
public class DelayPredictionRequest {
    private Integer hourOfDay;
    private Integer dayOfWeek;
    private String zone;
    private Long livreurId;
    private Integer nbArticles;
    private Double distanceKm;

    public DelayPredictionRequest() {}

    public Integer getHourOfDay() { return hourOfDay; }
    public void setHourOfDay(Integer hourOfDay) { this.hourOfDay = hourOfDay; }

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public Long getLivreurId() { return livreurId; }
    public void setLivreurId(Long livreurId) { this.livreurId = livreurId; }

    public Integer getNbArticles() { return nbArticles; }
    public void setNbArticles(Integer nbArticles) { this.nbArticles = nbArticles; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}
