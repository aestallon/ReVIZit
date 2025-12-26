package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.revizit.rest.model.WaterReportKind;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WaterReportDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class WaterReportDto {

  private WaterReportKind kind;

  private @Nullable Integer value;

  private @Nullable Long flavourId;

  public WaterReportDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public WaterReportDto(WaterReportKind kind) {
    this.kind = kind;
  }

  public WaterReportDto kind(WaterReportKind kind) {
    this.kind = kind;
    return this;
  }

  /**
   * Get kind
   * @return kind
   */
  @NotNull @Valid 
  @Schema(name = "kind", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("kind")
  public WaterReportKind getKind() {
    return kind;
  }

  public void setKind(WaterReportKind kind) {
    this.kind = kind;
  }

  public WaterReportDto value(@Nullable Integer value) {
    this.value = value;
    return this;
  }

  /**
   * The level of water in the gallon, as a percentage. Only applicable for PERCENTAGE reports. 
   * @return value
   */
  
  @Schema(name = "value", example = "75", description = "The level of water in the gallon, as a percentage. Only applicable for PERCENTAGE reports. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("value")
  public @Nullable Integer getValue() {
    return value;
  }

  public void setValue(@Nullable Integer value) {
    this.value = value;
  }

  public WaterReportDto flavourId(@Nullable Long flavourId) {
    this.flavourId = flavourId;
    return this;
  }

  /**
   * The flavour of the new gallon. Only applicable for SWAP reports. 
   * @return flavourId
   */
  
  @Schema(name = "flavourId", example = "1", description = "The flavour of the new gallon. Only applicable for SWAP reports. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("flavourId")
  public @Nullable Long getFlavourId() {
    return flavourId;
  }

  public void setFlavourId(@Nullable Long flavourId) {
    this.flavourId = flavourId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WaterReportDto waterReportDto = (WaterReportDto) o;
    return Objects.equals(this.kind, waterReportDto.kind) &&
        Objects.equals(this.value, waterReportDto.value) &&
        Objects.equals(this.flavourId, waterReportDto.flavourId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, value, flavourId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WaterReportDto {\n");
    sb.append("    kind: ").append(toIndentedString(kind)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    flavourId: ").append(toIndentedString(flavourId)).append("\n");
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

