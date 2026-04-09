package com.supervision.livraison.repository;

import com.supervision.livraison.entity.DeliveryProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryProofRepository extends JpaRepository<DeliveryProof, Long> {

    /**
     * Find all proofs for a specific delivery (commande).
     */
    @Query("SELECT dp FROM DeliveryProof dp WHERE dp.commande.nocde = :nocde ORDER BY dp.createdAt DESC")
    List<DeliveryProof> findByCommandeNocde(@Param("nocde") Long nocde);
}
