package com.supervision.livraison.repository;

import com.supervision.livraison.entity.GpsPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpsPositionRepository extends JpaRepository<GpsPosition, Long> {

    /**
     * Find the latest GPS position for a specific livreur.
     */
    @Query("SELECT g FROM GpsPosition g WHERE g.livreur.idpers = :livreurId ORDER BY g.recordedAt DESC")
    List<GpsPosition> findByLivreurIdOrderByRecordedAtDesc(@Param("livreurId") Long livreurId);

    /**
     * Find the most recent GPS position for each active livreur.
     * Uses a subquery to get the latest position per livreur.
     */
    @Query("SELECT g FROM GpsPosition g WHERE g.id IN " +
           "(SELECT MAX(g2.id) FROM GpsPosition g2 GROUP BY g2.livreur.idpers)")
    List<GpsPosition> findLatestPositions();
}
