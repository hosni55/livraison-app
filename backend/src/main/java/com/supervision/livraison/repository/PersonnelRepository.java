package com.supervision.livraison.repository;

import com.supervision.livraison.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    Optional<Personnel> findByLogin(String login);

    /**
     * Find all personnel with poste code 1 (LIVREUR role).
     */
    @Query("SELECT p FROM Personnel p WHERE p.poste.codeposte = 1")
    List<Personnel> findAllLivreurs();
}
