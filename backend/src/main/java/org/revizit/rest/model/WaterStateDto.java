package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WaterStateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class WaterStateDto {

  private Integer fullGallons;

  private Integer emptyGallons;

  private Integer waterLevel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime reportedAt;

  public WaterStateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public WaterStateDto(Integer fullGallons, Integer emptyGallons, Integer waterLevel, OffsetDateTime reportedAt) {
    this.fullGallons = fullGallons;
    this.emptyGallons = emptyGallons;
    this.waterLevel = waterLevel;
    this.reportedAt = reportedAt;
  }

  public WaterStateDto fullGallons(Integer fullGallons) {
    this.fullGallons = fullGallons;
    return this;
  }

  /**
   * Get fullGallons
   * minimum: 0
   * @return fullGallons
   */
  @NotNull @Min(value = 0) 
  @Schema(name = "fullGallons", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fullGallons")
  public Integer getFullGallons() {
    return fullGallons;
  }

  public void setFullGallons(Integer fullGallons) {
    this.fullGallons = fullGallons;
  }

  public WaterStateDto emptyGallons(Integer emptyGallons) {
    this.emptyGallons = emptyGallons;
    return this;
  }

  /**
   * Get emptyGallons
   * minimum: 0
   * @return emptyGallons
   */
  @NotNull @Min(value = 0) 
  @Schema(name = "emptyGallons", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("emptyGallons")
  public Integer getEmptyGallons() {
    return emptyGallons;
  }

  public void setEmptyGallons(Integer emptyGallons) {
    this.emptyGallons = emptyGallons;
  }

  public WaterStateDto waterLevel(Integer waterLevel) {
    this.waterLevel = waterLevel;
    return this;
  }

  /**
   * Get waterLevel
   * minimum: 0
   * maximum: 100
   * @return waterLevel
   */
  @NotNull @Min(value = 0) @Max(value = 100) 
  @Schema(name = "waterLevel", example = "75", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("waterLevel")
  public Integer getWaterLevel() {
    return waterLevel;
  }

  public void setWaterLevel(Integer waterLevel) {
    this.waterLevel = waterLevel;
  }

  public WaterStateDto reportedAt(OffsetDateTime reportedAt) {
    this.reportedAt = reportedAt;
    return this;
  }

  /**
   * Get reportedAt
   * @return reportedAt
   */
  @NotNull @Valid 
  @Schema(name = "reportedAt", example = "2021-01-01T00:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reportedAt")
  public OffsetDateTime getReportedAt() {
    return reportedAt;
  }

  public void setReportedAt(OffsetDateTime reportedAt) {
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
    WaterStateDto waterStateDto = (WaterStateDto) o;
    return Objects.equals(this.fullGallons, waterStateDto.fullGallons) &&
        Objects.equals(this.emptyGallons, waterStateDto.emptyGallons) &&
        Objects.equals(this.waterLevel, waterStateDto.waterLevel) &&
        Objects.equals(this.reportedAt, waterStateDto.reportedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullGallons, emptyGallons, waterLevel, reportedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WaterStateDto {\n");
    sb.append("    fullGallons: ").append(toIndentedString(fullGallons)).append("\n");
    sb.append("    emptyGallons: ").append(toIndentedString(emptyGallons)).append("\n");
    sb.append("    waterLevel: ").append(toIndentedString(waterLevel)).append("\n");
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

