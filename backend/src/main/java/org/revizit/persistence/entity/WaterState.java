package org.revizit.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_state")
@Getter
@Setter
public class WaterState {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Min(0)
  @Column(name = "empty_cnt", nullable = false)
  private int emptyCnt;

  @Min(0)
  @Column(name = "full_cnt", nullable = false)
  private int fullCnt;

  @Min(0)
  @Max(100)
  @Column(name = "curr_pct", nullable = false)
  private int currPct;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "curr_flav")
  private WaterFlavour currFlav;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "report")
  private WaterReport report;

}
