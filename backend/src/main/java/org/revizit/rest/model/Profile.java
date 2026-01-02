package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.revizit.rest.model.ProfileData;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Profile
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class Profile {

  private String username;

  private ProfileData data;

  private Boolean isAdmin = false;

  private @Nullable String pfp;

  public Profile() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Profile(String username, ProfileData data, Boolean isAdmin) {
    this.username = username;
    this.data = data;
    this.isAdmin = isAdmin;
  }

  public Profile username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
   */
  @NotNull 
  @Schema(name = "username", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Profile data(ProfileData data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
   */
  @NotNull @Valid 
  @Schema(name = "data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data")
  public ProfileData getData() {
    return data;
  }

  public void setData(ProfileData data) {
    this.data = data;
  }

  public Profile isAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
    return this;
  }

  /**
   * Get isAdmin
   * @return isAdmin
   */
  @NotNull 
  @Schema(name = "isAdmin", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isAdmin")
  public Boolean getIsAdmin() {
    return isAdmin;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public Profile pfp(@Nullable String pfp) {
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
    Profile profile = (Profile) o;
    return Objects.equals(this.username, profile.username) &&
        Objects.equals(this.data, profile.data) &&
        Objects.equals(this.isAdmin, profile.isAdmin) &&
        Objects.equals(this.pfp, profile.pfp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, data, isAdmin, pfp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Profile {\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    isAdmin: ").append(toIndentedString(isAdmin)).append("\n");
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

