package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * Notification entity — system notifications between personnel.
 */
@Entity
@Table(name = "NOTIFICATIONS")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notif_seq")
    @SequenceGenerator(name = "notif_seq", sequenceName = "SEQ_NOTIFICATIONS", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private Personnel sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Personnel receiver;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nocde")
    private Commande commande;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Personnel getSender() { return sender; }
    public void setSender(Personnel sender) { this.sender = sender; }

    public Personnel getReceiver() { return receiver; }
    public void setReceiver(Personnel receiver) { this.receiver = receiver; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
