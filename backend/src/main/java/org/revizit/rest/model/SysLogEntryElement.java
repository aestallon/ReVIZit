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
 * SysLogEntryElement
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")
public class SysLogEntryElement {

  private String qualifier;

  private String name;

  private String msg;

  public SysLogEntryElement() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SysLogEntryElement(String qualifier, String name, String msg) {
    this.qualifier = qualifier;
    this.name = name;
    this.msg = msg;
  }

  public SysLogEntryElement qualifier(String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  /**
   * Get qualifier
   * @return qualifier
   */
  @NotNull 
  @Schema(name = "qualifier", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("qualifier")
  public String getQualifier() {
    return qualifier;
  }

  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  public SysLogEntryElement name(String name) {
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

  public SysLogEntryElement msg(String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * Get msg
   * @return msg
   */
  @NotNull 
  @Schema(name = "msg", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("msg")
  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SysLogEntryElement sysLogEntryElement = (SysLogEntryElement) o;
    return Objects.equals(this.qualifier, sysLogEntryElement.qualifier) &&
        Objects.equals(this.name, sysLogEntryElement.name) &&
        Objects.equals(this.msg, sysLogEntryElement.msg);
  }

  @Override
  public int hashCode() {
    return Objects.hash(qualifier, name, msg);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SysLogEntryElement {\n");
    sb.append("    qualifier: ").append(toIndentedString(qualifier)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    msg: ").append(toIndentedString(msg)).append("\n");
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

