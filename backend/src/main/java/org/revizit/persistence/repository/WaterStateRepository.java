package org.revizit.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.revizit.persistence.entity.WaterState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterStateRepository extends JpaRepository<WaterState, Integer> {

  Optional<WaterState> findTopByOrderByCreatedAtDesc();

  @Query("""
      select   s
      from     WaterState s
      where    (cast(:from as localdatetime) is null or s.createdAt >= :from)
      and      (cast(:to as localdatetime) is null or s.createdAt <= :to)
      order by s.createdAt""")
  List<WaterState> findStateHistory(LocalDateTime from, LocalDateTime to);

  @Query("""
      select   s
      from     WaterState s
      where    s.createdAt >= ( select s_inner.createdAt
                                from   WaterState s_inner
                                where  s_inner.id = :id )
      order by s.createdAt asc""")
  List<WaterState> findStateHistorySince(int id);
}
