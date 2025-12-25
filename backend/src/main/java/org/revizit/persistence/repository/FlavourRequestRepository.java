package org.revizit.persistence.repository;

import org.revizit.persistence.entity.FlavourRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlavourRequestRepository extends JpaRepository<FlavourRequest, Integer> {
}
