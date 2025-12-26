package org.revizit.service;

import java.time.LocalDateTime;
import org.revizit.persistence.entity.ReportType;
import org.revizit.persistence.entity.WaterReport;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterReportRepository;
import org.revizit.persistence.repository.WaterStateRepository;
import org.revizit.rest.model.WaterReportDto;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaterService {

  private final UserService userService;
  private final WaterStateRepository waterStateRepository;
  private final WaterReportRepository waterReportRepository;
  private final WaterFlavourRepository waterFlavourRepository;


  public WaterState currentState() {
    return waterStateRepository.findTopByOrderByCreatedAtDesc().orElse(null);
  }

  public WaterReport registerAndAccept(WaterReportDto dto) {
    final var report = registerReport(dto);
    final var newState = currentState().accept(report);
    waterStateRepository.save(newState);
    return report;
  }

  public WaterReport registerReport(WaterReportDto dto) {

    final var report = new WaterReport();
    report.setReportedAt(LocalDateTime.now());
    report.setReportedBy(userService.currentUser());
    report.setVal(dto.getValue());
    switch (dto.getKind()) {
      case PERCENTAGE -> report.setKind(ReportType.SET_PERCENTAGE);
      case SWAP -> report.setKind(ReportType.BALLOON_CHANGE);
      case REFILL -> report.setKind(ReportType.BALLOON_REFILL);
    }
    return waterReportRepository.save(report);
  }

}
