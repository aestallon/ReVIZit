package org.revizit.persistence.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.revizit.rest.model.SysLogEntry;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sys_log")
@Getter
@Setter
public class SysLog {

  public static SysLog ofReportSubmitted(WaterReport report) {
    final var sysLog = new SysLog();
    sysLog.setAction("reportSubmitted");
    final var reporter = report.getReportedBy();
    if (reporter != null) {
      sysLog.setUsername(reporter.getUsername());
    }

    sysLog.setTimestamp(report.getReportedAt());
    final var element = new SysLogElement();
    element.setQualifier("report");
    element.setName("submission");
    element.setMsg(report.asMsgString());
    sysLog.getElements().add(element);
    return sysLog;
  }

  public static SysLog ofReportsRejected(List<WaterReport> reports) {
    final var rejecter = reports.getFirst().getRejectedBy().getUsername();
    final var sysLog = new SysLog();
    sysLog.setAction("reportsRejected");
    sysLog.setUsername(rejecter);
    sysLog.setTimestamp(reports.getFirst().getRejectedAt());

    for (var report : reports) {
      final var element = new SysLogElement();
      element.setQualifier("report");
      element.setName("rejection");
      element.setMsg(report.asMsgString());
      sysLog.getElements().add(element);
    }

    return sysLog;
  }

  public static SysLog ofReportsAccepted(List<WaterReport> reports) {
    final var accepter = reports.getFirst().getApprovedBy().getUsername();
    final var sysLog = new SysLog();
    sysLog.setAction("reportsAccepted");
    sysLog.setUsername(accepter);
    sysLog.setTimestamp(reports.getFirst().getApprovedAt());

    for (final var report : reports) {
      final var element = new SysLogElement();
      element.setQualifier("report");
      element.setName("acceptance");
      element.setMsg(report.asMsgString());
      sysLog.getElements().add(element);
    }

    return sysLog;
  }

  public static SysLog ofStateDefined(WaterState state) {
    final var creator = state.getReport().getReportedBy().getUsername();
    final var sysLog = new SysLog();
    sysLog.setAction("defineState");
    sysLog.setTimestamp(state.getReport().getReportedAt());
    sysLog.setUsername(creator);

    final var element = new SysLogElement();
    element.setQualifier("state");
    element.setName("definition");
    element.setMsg("Full: %d, Empty: %d, Pct: %d%%".formatted(
        state.getFullCnt(),
        state.getEmptyCnt(),
        state.getCurrPct()));
    sysLog.getElements().add(element);
    return sysLog;
  }

  public static SysLog ofUsersCreated(Collection<UserAccount> users,
                                      UserAccount creator,
                                      LocalDateTime now) {
    final var sysLog = new SysLog();
    sysLog.setTimestamp(now);
    sysLog.setAction("createUsers");
    sysLog.setUsername(creator.getUsername());

    for (final var user : users) {
      final var element = new SysLogElement();
      element.setQualifier("user");
      element.setName("creation");
      element.setMsg(user.getUsername() + " as " + user.getUserRole());
      sysLog.getElements().add(element);
    }

    return sysLog;
  }

  public static SysLog ofUserDeleted(UserAccount user, UserAccount deletedBy, LocalDateTime now) {
    final var sysLog = new SysLog();
    sysLog.setAction("deleteUser");
    sysLog.setTimestamp(now);
    sysLog.setUsername(deletedBy.getUsername());

    final var element = new SysLogElement();
    element.setQualifier("user");
    element.setName("deletion");
    element.setMsg((Objects.equals(user.getId(), deletedBy.getId()))
        ? "self-deletion"
        : user.getUsername());
    sysLog.getElements().add(element);

    return sysLog;
  }

  public static SysLog ofUserRoleChanged(UserAccount user,
                                         UserAccount changedBy,
                                         LocalDateTime now) {
    final var sysLog = new SysLog();
    sysLog.setTimestamp(now);
    sysLog.setAction("changeUserRole");
    sysLog.setUsername(changedBy.getUsername());

    final var element = new SysLogElement();
    element.setQualifier("user");
    element.setName("roleChange");
    element.setMsg(user.getUsername() + " to " + user.getUserRole());
    sysLog.getElements().add(element);

    return sysLog;
  }

  public static SysLog ofUserPasswordReset(UserAccount user,
                                           UserAccount changedBy,
                                           LocalDateTime now) {
    final var sysLog = new SysLog();
    sysLog.setAction("resetUserPassword");
    sysLog.setTimestamp(now);
    sysLog.setUsername(changedBy.getUsername());

    final var element = new SysLogElement();
    element.setQualifier("user");
    element.setName("passwordReset");
    element.setMsg(user.getUsername());
    sysLog.getElements().add(element);

    return sysLog;
  }

  public static SysLog ofUserLogIn(UserAccount user, LocalDateTime now) {
    final var sysLog = new SysLog();
    sysLog.setAction("userLogIn");
    sysLog.setTimestamp(now);
    sysLog.setUsername(user.getUsername());

    return sysLog;
  }


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(nullable = false)
  private String action;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "sys_log_element", joinColumns = @JoinColumn(name = "sys_log"))
  @OrderColumn(name = "order_num")
  private List<SysLogElement> elements = new ArrayList<>();

  public SysLogEntry toDto() {
    return new SysLogEntry()
        .timestamp(timestamp.atOffset(ZoneOffset.UTC))
        .action(action)
        .user(username)
        .elements(elements.stream().map(SysLogElement::toDto).toList());
  }

}
