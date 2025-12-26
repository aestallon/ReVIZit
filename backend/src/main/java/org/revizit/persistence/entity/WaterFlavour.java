package org.revizit.persistence.entity;

import org.revizit.rest.model.WaterFlavourDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "water_flavour")
@Getter
@Setter
public class WaterFlavour {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true)
  private String name;

  public WaterFlavourDto toDto() {
    return new WaterFlavourDto()
        .id(id.longValue())
        .name(name);
  }

}
