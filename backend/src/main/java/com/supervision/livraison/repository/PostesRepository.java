package com.supervision.livraison.repository;

import com.supervision.livraison.entity.Postes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostesRepository extends JpaRepository<Postes, Long> {
}
