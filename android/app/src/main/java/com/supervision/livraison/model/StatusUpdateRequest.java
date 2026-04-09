package com.supervision.livraison.model;

/**
 * Status update request for changing delivery status.
 */
public class StatusUpdateRequest {
    private String etatliv;
    private String remarque;

    public StatusUpdateRequest(String etatliv, String remarque) {
        this.etatliv = etatliv;
        this.remarque = remarque;
    }

    public String getEtatliv() { return etatliv; }
    public String getRemarque() { return remarque; }
}
