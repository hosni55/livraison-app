package com.supervision.livraison.dto;

/**
 * GPS position request DTO.
 */
public class GpsPositionRequest {
    private Double latitude;
    private Double longitude;

    public GpsPositionRequest() {}

    public GpsPositionRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
