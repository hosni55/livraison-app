package com.supervision.livraison.model;

/**
 * Client model for Android.
 */
public class Client {
    private Long noclt;
    private String nomclt;
    private String prenomclt;
    private String adrclt;
    private String villeclt;
    private String telclt;

    public Long getNoclt() { return noclt; }
    public void setNoclt(Long noclt) { this.noclt = noclt; }

    public String getNomclt() { return nomclt; }
    public void setNomclt(String nomclt) { this.nomclt = nomclt; }

    public String getPrenomclt() { return prenomclt; }
    public void setPrenomclt(String prenomclt) { this.prenomclt = prenomclt; }

    @Override
    public String toString() {
        return nomclt + " " + prenomclt;
    }
}
