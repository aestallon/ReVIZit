package org.revizit.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.revizit.persistence.entity.ReportType;
import org.revizit.persistence.entity.SysLog;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterReport;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterReportRepository;
import org.revizit.persistence.repository.WaterStateRepository;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterStateDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaterService {

  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;
  private final UserService userService;
  private final WaterStateRepository waterStateRepository;
  private final WaterReportRepository waterReportRepository;
  private final WaterFlavourRepository waterFlavourRepository;


  public WaterState currentState() {
    return waterStateRepository.findTopByOrderByCreatedAtDesc().orElse(null);
  }

  public List<WaterState> stateHistory(LocalDateTime from, LocalDateTime to) {
    return waterStateRepository.findStateHistory(from, to);
  }

  @Transactional
  public WaterState defineState(WaterStateDto dto) {
    if (!userService.isCurrentUserAdmin()) {
      throw new IllegalStateException("Only admins can define water state!");
    }

    final var flavourId = dto.getFlavour().getId();
    final var now = LocalDateTime.now(clock);
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
    var state = WaterState.builder()
        .currPct(dto.getWaterLevel())
        .emptyCnt(dto.getEmptyGallons())
        .fullCnt(dto.getFullGallons())
        .createdAt(now)
        .report(report)
        .build();
    state = waterStateRepository.save(state);
    eventPublisher.publishEvent(SysLog.ofStateDefined(state));
    return state;
  }

  @Transactional
  public WaterReport registerReport(WaterReportDto dto) {
    Objects.requireNonNull(dto, "Water Report DTO cannot be null!");

    final var currState = currentState();
    if (currState == null) {
      throw new IllegalStateException("Initial water state is not yet defined!");
    }

    final var currReport = currState.getReport();

    final var reportBuilder = WaterReport.builder()
        .reportedAt(LocalDateTime.now(clock))
        .reportedBy(userService.currentUser());
    switch (dto.getKind()) {
      case PERCENTAGE -> reportBuilder
          .kind(ReportType.SET_PERCENTAGE)
          .val(Objects.requireNonNull(dto.getValue(), "Water level cannot be null!"))
          .flavour(currReport.getFlavour());
      case SWAP -> reportBuilder
          .kind(ReportType.BALLOON_CHANGE)
          .val(WaterState.WATER_LEVEL_MAX)
          .flavour(getFlavour(dto.getFlavourId())
              .orElseThrow(() -> new IllegalArgumentException(
                  "Invalid flavour id: " + dto.getFlavourId())));
      case REFILL -> reportBuilder
          .kind(ReportType.BALLOON_REFILL)
          .val(currReport.getVal())
          .flavour(currReport.getFlavour());
    }
    final var report = waterReportRepository.save(reportBuilder.build());
    eventPublisher.publishEvent(SysLog.ofReportSubmitted(report));
    return report;
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
      throw new IllegalArgumentException("One or more reports are not pending!");
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
    if (state == null) {
      throw new IllegalStateException("Initial water state is not yet defined!");
    }

    final var now = LocalDateTime.now(clock);
    for (final var report : reportsToAccept) {

      state = switch (state.accept(report)) {
        case WaterState.ReportResult.New(var newState) -> {
          report.setApprovedAt(now);
          report.setApprovedBy(user);
          yield waterStateRepository.save(newState);
        }
        case WaterState.ReportResult.Rejection _ -> {
          report.setRejectedAt(now);
          report.setRejectedBy(user);
          waterReportRepository.save(report);
          yield state;
        }
      };
    }

    eventPublisher.publishEvent(SysLog.ofReportsAccepted(reportsToAccept));
    return state;
  }

  @Transactional
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

    final var now = LocalDateTime.now(clock);
    for (final var report : reportsToReject) {
      report.setRejectedAt(now);
      report.setRejectedBy(user);
    }
    waterReportRepository.saveAll(reportsToReject);
    eventPublisher.publishEvent(SysLog.ofReportsRejected(reportsToReject));
  }


  @Transactional(readOnly = true)
  public List<WaterFlavour> getAvailableFlavours() {
    return waterFlavourRepository.findByInactive(false);
  }

  public List<WaterFlavour> getAllFlavours() {
    return waterFlavourRepository.findAll();
  }

  public Optional<WaterFlavour> getFlavour(Long id) {
    Objects.requireNonNull(id, "Flavour id cannot be null!");
    return waterFlavourRepository.findById(id.intValue());
  }

  @Transactional
  public WaterFlavour createFlavour(final String name) {
    if (!userService.isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    if (getAllFlavours().stream().map(WaterFlavour::getName).collect(Collectors.toSet())
        .contains(name)) {
      throw new IllegalArgumentException("Water flavour with name " + name + " already exists!");
    }

    final var flavour = new WaterFlavour();
    flavour.setName(name);
    return waterFlavourRepository.save(flavour);
  }

  @Transactional
  public void deleteFlavour(final Long id) {
    if (!userService.isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    Set<Integer> usedFlavourIds = waterFlavourRepository.findUsedFlavourIds();
    if (usedFlavourIds.contains(id.intValue())) {
      WaterFlavour waterFlavour = waterFlavourRepository.findById(id.intValue()).orElseThrow();
      waterFlavour.setInactive(true);
      waterFlavourRepository.save(waterFlavour);
    } else {
      waterFlavourRepository.deleteById(id.intValue());
    }
  }

  @Transactional
  public WaterFlavour restoreFlavour(final Long id) {
    if (!userService.isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    final var flavour = waterFlavourRepository.findById(id.intValue())
        .orElseThrow(() -> new NotFoundException("Flavour not found!"));
    if (!flavour.isInactive()) {
      return flavour;
    }

    flavour.setInactive(false);
    return waterFlavourRepository.save(flavour);
  }

  @Transactional
  public WaterFlavour renameFlavour(final Long id, final String newName) {
    if (!userService.isCurrentUserAdmin()) {
      throw new NotAuthorisedException();
    }

    final var flavour = waterFlavourRepository
        .findById(id.intValue())
        .orElseThrow(() -> new NotFoundException("Flavour not found!"));
    if (newName.equals(flavour.getName())) {
      return flavour;
    }

    if (getAllFlavours().stream()
        .map(WaterFlavour::getName)
        .collect(Collectors.toSet())
        .contains(newName)) {
      throw new IllegalArgumentException("Water flavour with name " + newName + " already exists!");
    }

    flavour.setName(newName);
    return waterFlavourRepository.save(flavour);
  }

  public Set<Integer> getFlavoursInUse() {
    return waterFlavourRepository.findUsedFlavourIds();
  }

}
