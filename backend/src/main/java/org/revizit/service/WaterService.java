package org.revizit.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.revizit.persistence.entity.ReportType;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterReport;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterReportRepository;
import org.revizit.persistence.repository.WaterStateRepository;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterStateDto;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
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

  public List<WaterState> stateHistory(LocalDateTime from, LocalDateTime to) {
    return waterStateRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
  }

  public WaterState defineState(WaterStateDto dto, long flavourId) {
    if (!userService.isCurrentUserAdmin()) {
      throw new IllegalStateException("Only admins can define water state!");
    }

    final var now = LocalDateTime.now();
    var report = WaterReport.builder()
        .reportedAt(now)
        .reportedBy(userService.currentUser())
        .kind(ReportType.SET_PERCENTAGE)
        .approvedAt(now)
        .approvedBy(userService.currentUser())
        .val(dto.getWaterLevel())
        .flavour(getFlavour(flavourId).orElseThrow())
        .build();
    report = waterReportRepository.save(report);
    final var state = WaterState.builder()
        .currPct(dto.getWaterLevel())
        .emptyCnt(dto.getEmptyGallons())
        .fullCnt(dto.getFullGallons())
        .createdAt(now)
        .report(report)
        .build();
    return waterStateRepository.save(state);
  }

  public WaterReport registerReport(WaterReportDto dto) {
    Objects.requireNonNull(dto, "Water Report DTO cannot be null!");

    final var currState = currentState();
    final var currReport = currState.getReport();

    final var reportBuilder = WaterReport.builder()
        .reportedAt(LocalDateTime.now())
        .reportedBy(userService.currentUser());
    switch (dto.getKind()) {
      case PERCENTAGE -> reportBuilder
          .kind(ReportType.SET_PERCENTAGE)
          .val(Objects.requireNonNull(dto.getValue(), "Water level cannot be null!"))
          .flavour(currReport.getFlavour());
      case SWAP -> reportBuilder
          .kind(ReportType.BALLOON_CHANGE)
          .val(100)
          .flavour(getFlavour(dto.getFlavourId())
              .orElseThrow(() -> new IllegalArgumentException(
                  "Invalid flavour id: " + dto.getFlavourId())));
      case REFILL -> reportBuilder
          .kind(ReportType.BALLOON_REFILL)
          .val(currReport.getVal())
          .flavour(currReport.getFlavour());
    }
    return waterReportRepository.save(reportBuilder.build());
  }

  public List<WaterReport> getPendingReports() {
    return waterReportRepository.findByApprovedByIsNullAndRejectedByIsNullOrderByReportedAtAsc();
  }

  @Transactional
  public WaterState acceptReports(List<Long> ids) {
    final var user = userService.currentUser();
    if (user == null) {
      throw new IllegalStateException("Log-in is required to accept reports!");
    }

    final var pendingReports = getPendingReports();
    if (ids.size() > pendingReports.size()) {
      throw new IllegalArgumentException("Too many ids!");
    }

    List<WaterReport> reportsToAccept = new ArrayList<>(ids.size());
    for (int i = 0; i < ids.size(); i++) {
      final var reportId = pendingReports.get(i).getId();
      final var id = ids.get(i);
      if (reportId.longValue() != id) {
        throw new IllegalArgumentException("Invalid id!");
      }

      reportsToAccept.add(pendingReports.get(i));
    }

    var state = currentState();
    final var now = LocalDateTime.now();
    for (final var report : reportsToAccept) {
      report.setApprovedAt(now);
      report.setApprovedBy(user);
      state = currentState().accept(report);
      waterStateRepository.save(state);
    }
    return state;
  }

  public void rejectReports(List<Long> ids) {
    final var user = userService.currentUser();
    if (user == null) {
      throw new IllegalStateException("Log-in is required to reject reports!");
    }

    final var pendingReports = getPendingReports().stream()
        .collect(Collectors.toMap(WaterReport::getId, r -> r));
    final var reportsToReject = new ArrayList<WaterReport>(ids.size());
    for (final var id : ids) {
      WaterReport waterReport = pendingReports.get(id.intValue());
      if (waterReport == null) {
        throw new IllegalArgumentException("Invalid id [ " + id + " ] is not pending report!");
      }
      reportsToReject.add(waterReport);
    }

    final var now = LocalDateTime.now();
    for (final var report : reportsToReject) {
      report.setRejectedAt(now);
      report.setRejectedBy(user);
    }
    waterReportRepository.saveAll(reportsToReject);
  }


  public List<WaterFlavour> getFlavours() {
    return waterFlavourRepository.findAll();
  }

  public Optional<WaterFlavour> getFlavour(Long id) {
    Objects.requireNonNull(id, "Flavour id cannot be null!");
    return waterFlavourRepository.findById(id.intValue());
  }

}
