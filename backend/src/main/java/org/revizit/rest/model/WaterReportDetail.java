package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.revizit.rest.model.WaterReportDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WaterReportDetail
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class WaterReportDetail {

  private @Nullable WaterReportDto waterReport;

  private @Nullable Long id;

  private @Nullable String reportedBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime reportedAt;

  public WaterReportDetail waterReport(@Nullable WaterReportDto waterReport) {
    this.waterReport = waterReport;
    return this;
  }

  /**
   * Get waterReport
   * @return waterReport
   */
  @Valid 
  @Schema(name = "waterReport", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("waterReport")
  public @Nullable WaterReportDto getWaterReport() {
    return waterReport;
  }

  public void setWaterReport(@Nullable WaterReportDto waterReport) {
    this.waterReport = waterReport;
  }

  public WaterReportDetail id(@Nullable Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable Long getId() {
    return id;
  }

  public void setId(@Nullable Long id) {
    this.id = id;
  }

  public WaterReportDetail reportedBy(@Nullable String reportedBy) {
    this.reportedBy = reportedBy;
    return this;
  }

  /**
   * Get reportedBy
   * @return reportedBy
   */
  
  @Schema(name = "reportedBy", example = "John Doe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reportedBy")
  public @Nullable String getReportedBy() {
    return reportedBy;
  }

  public void setReportedBy(@Nullable String reportedBy) {
    this.reportedBy = reportedBy;
  }

  public WaterReportDetail reportedAt(@Nullable OffsetDateTime reportedAt) {
    this.reportedAt = reportedAt;
    return this;
  }

  /**
   * Get reportedAt
   * @return reportedAt
   */
  @Valid 
  @Schema(name = "reportedAt", example = "2021-01-01T00:00Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reportedAt")
  public @Nullable OffsetDateTime getReportedAt() {
    return reportedAt;
  }

  public void setReportedAt(@Nullable OffsetDateTime reportedAt) {
    this.reportedAt = reportedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WaterReportDetail waterReportDetail = (WaterReportDetail) o;
    return Objects.equals(this.waterReport, waterReportDetail.waterReport) &&
        Objects.equals(this.id, waterReportDetail.id) &&
        Objects.equals(this.reportedBy, waterReportDetail.reportedBy) &&
        Objects.equals(this.reportedAt, waterReportDetail.reportedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(waterReport, id, reportedBy, reportedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WaterReportDetail {\n");
    sb.append("    waterReport: ").append(toIndentedString(waterReport)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    reportedBy: ").append(toIndentedString(reportedBy)).append("\n");
    sb.append("    reportedAt: ").append(toIndentedString(reportedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

