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
 * ProfileThumbnail
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class ProfileThumbnail {

  private String name;

  private @Nullable String pfp;

  public ProfileThumbnail() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProfileThumbnail(String name) {
    this.name = name;
  }

  public ProfileThumbnail name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProfileThumbnail pfp(@Nullable String pfp) {
    this.pfp = pfp;
    return this;
  }

  /**
   * Get pfp
   * @return pfp
   */
  
  @Schema(name = "pfp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pfp")
  public @Nullable String getPfp() {
    return pfp;
  }

  public void setPfp(@Nullable String pfp) {
    this.pfp = pfp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProfileThumbnail profileThumbnail = (ProfileThumbnail) o;
    return Objects.equals(this.name, profileThumbnail.name) &&
        Objects.equals(this.pfp, profileThumbnail.pfp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pfp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileThumbnail {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    pfp: ").append(toIndentedString(pfp)).append("\n");
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

