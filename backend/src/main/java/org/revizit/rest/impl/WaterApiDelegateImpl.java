package org.revizit.rest.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import org.revizit.event.PendingReportsChanged;
import org.revizit.event.WaterStateChanged;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterReport;
import org.revizit.persistence.entity.WaterState;
import org.revizit.rest.api.WaterApiDelegate;
import org.revizit.rest.model.WaterFlavourDto;
import org.revizit.rest.model.WaterReportDetail;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterStateDetail;
import org.revizit.rest.model.WaterStateDto;
import org.revizit.service.WaterService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaterApiDelegateImpl implements WaterApiDelegate {

  private final WaterService waterService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public ResponseEntity<WaterStateDto> getCurrentWaterState() {
    final var currState = waterService.currentState();
    if (currState == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(currState.toDto());
  }

  @Override
  public ResponseEntity<List<WaterReportDetail>> getPendingWaterReports() {
    return waterService.getPendingReports().stream()
        .map(WaterReport::toDetail)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<Void> approveWaterReport(List<Long> id) {
    waterService.acceptReports(id);
    eventPublisher.publishEvent(new PendingReportsChanged());
    eventPublisher.publishEvent(new WaterStateChanged());
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> rejectWaterReport(List<Long> id) {
    waterService.rejectReports(id);
    eventPublisher.publishEvent(new PendingReportsChanged());
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<WaterFlavourDto>> getWaterFlavours() {
    return waterService.getAvailableFlavours().stream()
        .map(WaterFlavour::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<WaterFlavourDto> createWaterFlavour(String body) {
    final var flavour = waterService.createFlavour(body);
    return ResponseEntity.ok(flavour.toDto());
  }

  @Override
  public ResponseEntity<WaterStateDetail> defineCurrentWaterState(WaterStateDto waterStateDto) {
    final var state = waterService.defineState(waterStateDto);
    eventPublisher.publishEvent(new WaterStateChanged());
    return ResponseEntity.ok(state.toDetail());
  }

  @Override
  public ResponseEntity<Void> deleteWaterFlavour(Long id) {
    waterService.deleteFlavour(id);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<WaterFlavourDto>> getAllWaterFlavours() {
    return waterService.getAllFlavours().stream()
        .map(WaterFlavour::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<List<Long>> getInUseWaterFlavours() {
    return waterService.getFlavoursInUse().stream()
        .map(Integer::longValue)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<WaterFlavourDto> updateWaterFlavour(Long id,
                                                            WaterFlavourDto waterFlavourDto) {
    final var flavour = waterService.renameFlavour(id, waterFlavourDto.getName());
    return ResponseEntity.ok(flavour.toDto());
  }

  @Override
  public ResponseEntity<List<WaterStateDetail>> getWaterStates(OffsetDateTime from,
                                                               OffsetDateTime to) {
    final var fromLdt = from == null ? null : from.toLocalDateTime();
    final var toLdt = to == null ? null : to.toLocalDateTime();
    return waterService.stateHistory(fromLdt, toLdt).stream()
        .map(WaterState::toDetail)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<WaterReportDetail> submitWaterReport(WaterReportDto waterReportDto) {
    final var report = waterService.registerReport(waterReportDto);
    eventPublisher.publishEvent(new PendingReportsChanged());
    return ResponseEntity.ok(report.toDetail());
  }

  @Override
  public ResponseEntity<WaterFlavourDto> activateWaterFlavour(Long id) {
    final var flavour = waterService.restoreFlavour(id);
    return ResponseEntity.ok(flavour.toDto());
  }
}
