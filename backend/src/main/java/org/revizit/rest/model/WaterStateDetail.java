package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.revizit.rest.model.WaterStateDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WaterStateDetail
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class WaterStateDetail {

  private WaterStateDto waterState;

  private Long id;

  private @Nullable String reportedBy;

  public WaterStateDetail() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public WaterStateDetail(WaterStateDto waterState, Long id) {
    this.waterState = waterState;
    this.id = id;
  }

  public WaterStateDetail waterState(WaterStateDto waterState) {
    this.waterState = waterState;
    return this;
  }

  /**
   * Get waterState
   * @return waterState
   */
  @NotNull @Valid 
  @Schema(name = "waterState", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("waterState")
  public WaterStateDto getWaterState() {
    return waterState;
  }

  public void setWaterState(WaterStateDto waterState) {
    this.waterState = waterState;
  }

  public WaterStateDetail id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public WaterStateDetail reportedBy(@Nullable String reportedBy) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WaterStateDetail waterStateDetail = (WaterStateDetail) o;
    return Objects.equals(this.waterState, waterStateDetail.waterState) &&
        Objects.equals(this.id, waterStateDetail.id) &&
        Objects.equals(this.reportedBy, waterStateDetail.reportedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(waterState, id, reportedBy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WaterStateDetail {\n");
    sb.append("    waterState: ").append(toIndentedString(waterState)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    reportedBy: ").append(toIndentedString(reportedBy)).append("\n");
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

