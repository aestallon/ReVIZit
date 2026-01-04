package org.revizit.service;

import java.time.LocalDateTime;
import java.util.List;
import org.revizit.persistence.entity.SysLog;
import org.revizit.persistence.repository.SysLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemLogService {

  private final UserService userService;
  private final SysLogRepository sysLogRepository;

  @EventListener(SysLog.class)
  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void log(SysLog event) {

    sysLogRepository.save(event);
  }

  public List<SysLog> fetchLogs(LocalDateTime from, LocalDateTime to) {
    if (!userService.isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    return sysLogRepository.findLogHistory(from, to);
  }

}
