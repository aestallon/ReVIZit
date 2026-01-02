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

  public static final int GALLON_COUNT_MIN = 0;
  public static final int WATER_LEVEL_MIN = 0;
  public static final int WATER_LEVEL_MAX = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Min(value = GALLON_COUNT_MIN, message = "{water-state.empty-count.min}")
  @Column(name = "empty_cnt", nullable = false)
  private int emptyCnt;

  @Min(value = GALLON_COUNT_MIN, message = "{water-state.full-count.min}")
  @Column(name = "full_cnt", nullable = false)
  private int fullCnt;

  @Min(value = WATER_LEVEL_MIN, message = "{water-state.curr-percentage.min}")
  @Max(value = WATER_LEVEL_MAX, message = "{water-state.curr-percentage.max}")
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
    final var reporter = UserAccount.extractDisplayName(reportedBy);
    return new WaterStateDetail()
        .waterState(toDto())
        .id(id.longValue())
        .reportedBy(reporter);
  }

  public ReportResult accept(WaterReport report) {
    final var result = switch (report.getKind()) {
      case SET_PERCENTAGE -> {
        final var newState = new WaterState();
        newState.setEmptyCnt(emptyCnt);
        newState.setFullCnt(fullCnt);
        newState.setCurrPct(report.getVal());
        report.setFlavour(this.report.getFlavour());
        yield new ReportResult.New(newState);
      }
      case BALLOON_CHANGE -> {
        final var newState = new WaterState();
        newState.setEmptyCnt(emptyCnt + 1);
        newState.setFullCnt(fullCnt - 1);
        newState.setCurrPct(WATER_LEVEL_MAX);
        yield new ReportResult.New(newState);
      }
      case BALLOON_REFILL -> {
        if (emptyCnt == GALLON_COUNT_MIN) {
          yield ReportResult.Rejection.INSTANCE;
        }

        final var newState = new WaterState();
        newState.setEmptyCnt(GALLON_COUNT_MIN);
        newState.setFullCnt(fullCnt + emptyCnt);
        newState.setCurrPct(currPct);
        report.setFlavour(this.report.getFlavour());
        yield new ReportResult.New(newState);
      }
    };

    if (result instanceof ReportResult.New(var state)) {
      state.setReport(report);
      state.setCreatedAt(report.getReportedAt());
    }

    return result;
  }

  public sealed interface ReportResult {

    record Rejection() implements ReportResult {
      public static final ReportResult INSTANCE = new Rejection();
    }


    record New(WaterState state) implements ReportResult {
    }

  }

}
