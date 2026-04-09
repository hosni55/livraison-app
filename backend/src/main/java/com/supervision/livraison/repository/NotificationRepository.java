package com.supervision.livraison.repository;

import com.supervision.livraison.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a receiver, ordered by creation date.
     */
    @Query("SELECT n FROM Notification n WHERE n.receiver.idpers = :receiverId ORDER BY n.createdAt DESC")
    List<Notification> findByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * Find unread notifications for a receiver.
     */
    @Query("SELECT n FROM Notification n WHERE n.receiver.idpers = :receiverId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * Count unread notifications for a receiver.
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.idpers = :receiverId AND n.isRead = false")
    Long countUnreadByReceiverId(@Param("receiverId") Long receiverId);
}
