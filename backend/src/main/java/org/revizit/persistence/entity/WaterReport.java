package org.revizit.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.revizit.rest.model.WaterReportDetail;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterReportKind;

@Entity
@Table(name = "water_report")
@Getter
@Setter
public class WaterReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(nullable = false)
  private ReportType kind;

  @Min(0)
  @Max(100)
  private int val;

  @Column(name = "reported_at", nullable = false)
  private LocalDateTime reportedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_by", nullable = false)
  private UserAccount reportedBy;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private UserAccount approvedBy;

  public WaterReportDetail toDetail() {
    final var reporter = reportedBy != null ? reportedBy.getUsername() : "anonymous";
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
            // TODO: FIX
            .flavourId(1L));
  }
}
