package org.revizit.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.revizit.persistence.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SysLogRepository extends JpaRepository<SysLog, Integer> {

  @Query("""
      select   l
      from     SysLog l
      where    (cast(:from as localdatetime ) is null or l.timestamp >= :from)
      and      (cast(:to as localdatetime ) is null or l.timestamp <= :to)
      order by l.timestamp asc""")
  List<SysLog> findLogHistory(LocalDateTime from, LocalDateTime to);
}
