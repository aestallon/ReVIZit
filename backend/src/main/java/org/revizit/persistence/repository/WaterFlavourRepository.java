package org.revizit.persistence.repository;

import org.revizit.persistence.entity.WaterFlavour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterFlavourRepository extends JpaRepository<WaterFlavour, Integer> {
}
