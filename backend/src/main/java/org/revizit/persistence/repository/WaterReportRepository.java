package org.revizit.persistence.repository;

import org.revizit.persistence.entity.WaterReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterReportRepository extends JpaRepository<WaterReport, Integer> {
}
