package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * WaterFlavourDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class WaterFlavourDto {

  private Long id;

  private String name;

  private Boolean inactive = false;

  public WaterFlavourDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public WaterFlavourDto(Long id, String name, Boolean inactive) {
    this.id = id;
    this.name = name;
    this.inactive = inactive;
  }

  public WaterFlavourDto id(Long id) {
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

  public WaterFlavourDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Regular", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public WaterFlavourDto inactive(Boolean inactive) {
    this.inactive = inactive;
    return this;
  }

  /**
   * Get inactive
   * @return inactive
   */
  @NotNull 
  @Schema(name = "inactive", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("inactive")
  public Boolean getInactive() {
    return inactive;
  }

  public void setInactive(Boolean inactive) {
    this.inactive = inactive;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WaterFlavourDto waterFlavourDto = (WaterFlavourDto) o;
    return Objects.equals(this.id, waterFlavourDto.id) &&
        Objects.equals(this.name, waterFlavourDto.name) &&
        Objects.equals(this.inactive, waterFlavourDto.inactive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, inactive);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WaterFlavourDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    inactive: ").append(toIndentedString(inactive)).append("\n");
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

