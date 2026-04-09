package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * GpsPosition entity — GPS coordinates recorded by livreurs.
 */
@Entity
@Table(name = "GPS_POSITIONS")
public class GpsPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gps_seq")
    @SequenceGenerator(name = "gps_seq", sequenceName = "SEQ_GPS_POSITIONS", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livreur_id", nullable = false)
    private Personnel livreur;

    @Column(name = "latitude", nullable = false, columnDefinition = "NUMBER(10,8)")
    private Double latitude;

    @Column(name = "longitude", nullable = false, columnDefinition = "NUMBER(11,8)")
    private Double longitude;

    @Column(name = "recorded_at")
    private Timestamp recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Personnel getLivreur() { return livreur; }
    public void setLivreur(Personnel livreur) { this.livreur = livreur; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Timestamp getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Timestamp recordedAt) { this.recordedAt = recordedAt; }
}
