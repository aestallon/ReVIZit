package org.revizit.persistence.entity;

import org.revizit.rest.model.SysLogEntryElement;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Table(name = "sys_log_element")
@Getter
@Setter
public class SysLogElement {

  @Column(nullable = false)
  private String qualifier;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String msg;


  public SysLogEntryElement toDto() {
    return new SysLogEntryElement(qualifier, name, msg);
  }

}
