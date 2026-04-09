package com.supervision.livraison.dto;

/**
 * Status update request DTO.
 */
public class StatusUpdateRequest {
    private String etatliv;
    private String remarque;

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(String etatliv, String remarque) {
        this.etatliv = etatliv;
        this.remarque = remarque;
    }

    public String getEtatliv() { return etatliv; }
    public void setEtatliv(String etatliv) { this.etatliv = etatliv; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}
