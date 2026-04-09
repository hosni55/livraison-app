package com.supervision.livraison.repository;

import com.supervision.livraison.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages between two personnel (bidirectional).
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.idpers = :user1Id AND m.receiver.idpers = :user2Id) OR " +
           "(m.sender.idpers = :user2Id AND m.receiver.idpers = :user1Id) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * Find messages for a specific commande.
     */
    @Query("SELECT m FROM Message m WHERE m.commande.nocde = :nocde ORDER BY m.createdAt ASC")
    List<Message> findByCommandeNocde(@Param("nocde") Long nocde);
}
