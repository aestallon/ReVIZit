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
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.revizit.rest.model.WaterReportDetail;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterReportKind;
import org.springframework.data.annotation.PersistenceCreator;

@Entity
@Table(name = "water_report")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class WaterReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(nullable = false)
  private ReportType kind;

  @Min(WaterState.WATER_LEVEL_MIN)
  @Max(WaterState.WATER_LEVEL_MAX)
  private int val;

  @Column(name = "reported_at", nullable = false)
  private LocalDateTime reportedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "reported_by")
  private UserAccount reportedBy;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "approved_by")
  private UserAccount approvedBy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "flavour")
  private WaterFlavour flavour;

  @Column(name = "rejected_at")
  private LocalDateTime rejectedAt;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rejected_by")
  private UserAccount rejectedBy;

  @PersistenceCreator
  public WaterReport() {
    // empty for JPA
  }

  public WaterReportDetail toDetail() {
    final var reporter = UserAccount.extractDisplayName(reportedBy);
    final var flavourId = flavour != null ? flavour.getId() : -1L;
    return new WaterReportDetail()
        .id(id.longValue())
        .reportedAt(reportedAt.atOffset(ZoneOffset.UTC))
        .reportedBy(reporter)
        .waterReport(new WaterReportDto()
            .kind(switch (kind) {
              case BALLOON_REFILL -> WaterReportKind.REFILL;
              case BALLOON_CHANGE -> WaterReportKind.SWAP;
              case SET_PERCENTAGE -> WaterReportKind.PERCENTAGE;
            })
            .value(val)
            .flavourId(flavourId));
  }
}
