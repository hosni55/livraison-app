package com.supervision.livraison.model;

/**
 * Livreur model for driver information with GPS position.
 */
public class Livreur {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String ville;
    private Double latitude;
    private Double longitude;
    private String lastUpdate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
}
