package org.revizit.rest.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.revizit.rest.model.SysLogEntryElement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SysLogEntry
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class SysLogEntry {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;

  private String user;

  private String action;

  @Valid
  private List<@Valid SysLogEntryElement> elements = new ArrayList<>();

  public SysLogEntry() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SysLogEntry(OffsetDateTime timestamp, String user, String action, List<@Valid SysLogEntryElement> elements) {
    this.timestamp = timestamp;
    this.user = user;
    this.action = action;
    this.elements = elements;
  }

  public SysLogEntry timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @NotNull @Valid 
  @Schema(name = "timestamp", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public SysLogEntry user(String user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
   */
  @NotNull 
  @Schema(name = "user", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("user")
  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public SysLogEntry action(String action) {
    this.action = action;
    return this;
  }

  /**
   * Get action
   * @return action
   */
  @NotNull 
  @Schema(name = "action", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("action")
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public SysLogEntry elements(List<@Valid SysLogEntryElement> elements) {
    this.elements = elements;
    return this;
  }

  public SysLogEntry addElementsItem(SysLogEntryElement elementsItem) {
    if (this.elements == null) {
      this.elements = new ArrayList<>();
    }
    this.elements.add(elementsItem);
    return this;
  }

  /**
   * Get elements
   * @return elements
   */
  @NotNull @Valid 
  @Schema(name = "elements", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("elements")
  public List<@Valid SysLogEntryElement> getElements() {
    return elements;
  }

  public void setElements(List<@Valid SysLogEntryElement> elements) {
    this.elements = elements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SysLogEntry sysLogEntry = (SysLogEntry) o;
    return Objects.equals(this.timestamp, sysLogEntry.timestamp) &&
        Objects.equals(this.user, sysLogEntry.user) &&
        Objects.equals(this.action, sysLogEntry.action) &&
        Objects.equals(this.elements, sysLogEntry.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, user, action, elements);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SysLogEntry {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
    sb.append("    elements: ").append(toIndentedString(elements)).append("\n");
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

