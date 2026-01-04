package org.revizit.rest.impl;

import java.time.OffsetDateTime;
import java.util.List;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import org.revizit.persistence.entity.SysLog;
import org.revizit.rest.api.SysLogApiDelegate;
import org.revizit.rest.model.SysLogEntry;
import org.revizit.service.SystemLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SysLogApiDelegateImpl implements SysLogApiDelegate {

  private final SystemLogService systemLogService;

  @Override
  public ResponseEntity<List<SysLogEntry>> getSysLogs(OffsetDateTime from, OffsetDateTime to) {
    final var fromLdt = from == null ? null : from.toLocalDateTime();
    final var toLdt = to == null ? null : to.toLocalDateTime();
    return systemLogService.fetchLogs(fromLdt, toLdt).stream()
        .map(SysLog::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

}
