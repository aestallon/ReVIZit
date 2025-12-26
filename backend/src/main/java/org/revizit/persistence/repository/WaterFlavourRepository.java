package org.revizit.persistence.repository;

import java.util.Set;
import org.revizit.persistence.entity.WaterFlavour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterFlavourRepository extends JpaRepository<WaterFlavour, Integer> {

  void deleteByName(String name);

  @NativeQuery("""
      select distinct f.id
      from            water_report r
      left join       water_flavour f on r.flavour = f.id
      where           r.rejected_by is null""")
  Set<Integer> findUsedFlavourIds();

}
