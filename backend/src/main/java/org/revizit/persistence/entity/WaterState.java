package org.revizit.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.revizit.rest.model.WaterStateDetail;
import org.revizit.rest.model.WaterStateDto;
import org.springframework.data.annotation.PersistenceCreator;

@Entity
@Table(name = "water_state")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class WaterState {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Min(value = 0,message = "{water-state.empty-count.min}")
  @Column(name = "empty_cnt", nullable = false)
  private int emptyCnt;

  @Min(value = 0, message = "{water-state.full-count.min}")
  @Column(name = "full_cnt", nullable = false)
  private int fullCnt;

  @Min(value = 0, message = "{water-state.curr-percentage.min}")
  @Max(value = 100, message = "{water-state.curr-percentage.max}")
  @Column(name = "curr_pct", nullable = false)
  private int currPct;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "report")
  private WaterReport report;

  @PersistenceCreator
  public WaterState() {
    // empty for JPA
  }

  public WaterStateDto toDto() {
    return new WaterStateDto()
        .emptyGallons(emptyCnt)
        .fullGallons(fullCnt)
        .waterLevel(currPct)
        .reportedAt(createdAt.atOffset(ZoneOffset.UTC))
        .flavour(report.getFlavour().toDto());
  }

  public WaterStateDetail toDetail() {
    final var reportedBy = report.getReportedBy();
    final var reporter = reportedBy != null ? reportedBy.getUsername() : "anonymous";
    return new WaterStateDetail()
        .waterState(toDto())
        .id(id.longValue())
        .reportedBy(reporter);
  }

  public WaterState accept(WaterReport report) {
    final var state = switch (report.getKind()) {
      case SET_PERCENTAGE -> {
        final var newState = new WaterState();
        newState.setEmptyCnt(emptyCnt);
        newState.setFullCnt(fullCnt);
        newState.setCurrPct(report.getVal());
        report.setFlavour(this.report.getFlavour());
        yield newState;
      }
      case BALLOON_CHANGE -> {
        final var newState = new WaterState();
        newState.setEmptyCnt(emptyCnt + 1);
        newState.setFullCnt(fullCnt - 1);
        newState.setCurrPct(100);
        yield newState;
      }
      case BALLOON_REFILL -> {
        final var newState = new WaterState();
        newState.setEmptyCnt(0);
        newState.setFullCnt(fullCnt + emptyCnt);
        newState.setCurrPct(currPct);
        report.setFlavour(this.report.getFlavour());
        yield newState;
      }
    };
    state.setReport(report);
    state.setCreatedAt(report.getReportedAt());
    return state;
  }

}
