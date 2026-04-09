package com.supervision.livraison.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

/**
 * Message entity — chat messages between personnel.
 */
@Entity
@Table(name = "MESSAGES")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq")
    @SequenceGenerator(name = "msg_seq", sequenceName = "SEQ_MESSAGES", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    private Personnel sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Personnel receiver;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nocde")
    private Commande commande;

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

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
