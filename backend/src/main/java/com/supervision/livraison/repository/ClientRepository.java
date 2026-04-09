package com.supervision.livraison.repository;

import com.supervision.livraison.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Client entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
