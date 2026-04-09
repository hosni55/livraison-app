package com.supervision.livraison.repository;

import com.supervision.livraison.entity.LivraisonCom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<LivraisonCom, Long> {

    /**
     * Find all deliveries for a specific livreur.
     */
    @Query("SELECT l FROM LivraisonCom l WHERE l.livreur.idpers = :livreurId ORDER BY l.dateliv")
    List<LivraisonCom> findByLivreurId(@Param("livreurId") Long livreurId);

    /**
     * Find all deliveries for today.
     */
    @Query("SELECT l FROM LivraisonCom l WHERE l.dateliv >= :startDate AND l.dateliv < :endDate ORDER BY l.dateliv")
    List<LivraisonCom> findTodayDeliveries(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * Find deliveries by status.
     */
    List<LivraisonCom> findByEtatliv(String etatliv);

    /**
     * Find deliveries by livreur and status.
     */
    @Query("SELECT l FROM LivraisonCom l WHERE l.livreur.idpers = :livreurId AND l.etatliv = :etatliv ORDER BY l.dateliv")
    List<LivraisonCom> findByLivreurIdAndEtatliv(@Param("livreurId") Long livreurId, @Param("etatliv") String etatliv);

    /**
     * Find deliveries by date range.
     */
    @Query("SELECT l FROM LivraisonCom l WHERE l.dateliv BETWEEN :startDate AND :endDate ORDER BY l.dateliv")
    List<LivraisonCom> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * Count deliveries by status for dashboard.
     */
    @Query("SELECT l.etatliv, COUNT(l) FROM LivraisonCom l GROUP BY l.etatliv")
    List<Object[]> countByStatus();

    /**
     * Count deliveries by livreur for dashboard.
     */
    @Query("SELECT l.livreur.idpers, l.livreur.nompers, l.livreur.prenompers, COUNT(l), " +
           "SUM(CASE WHEN l.etatliv = 'LIVREE' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN l.etatliv = 'ECHEC' THEN 1 ELSE 0 END) " +
           "FROM LivraisonCom l GROUP BY l.livreur.idpers, l.livreur.nompers, l.livreur.prenompers")
    List<Object[]> countByLivreur();

    /**
     * Count deliveries by client city for dashboard.
     */
    @Query("SELECT c.villeclt, COUNT(l) FROM LivraisonCom l JOIN l.commande co JOIN co.client c GROUP BY c.villeclt")
    List<Object[]> countByClientVille();
}
