package com.supervision.livraison.dto;

/**
 * Chat assistant request DTO.
 */
public class ChatAssistantRequest {
    private String message;
    private Long nocde;
    private Long livreurId;

    public ChatAssistantRequest() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getNocde() { return nocde; }
    public void setNocde(Long nocde) { this.nocde = nocde; }

    public Long getLivreurId() { return livreurId; }
    public void setLivreurId(Long livreurId) { this.livreurId = livreurId; }
}
