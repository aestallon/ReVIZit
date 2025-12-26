package org.revizit.rest.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaterApiDelegateImpl implements WaterApiDelegate {

  private final WaterService waterService;

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
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> rejectWaterReport(List<Long> id) {
    waterService.rejectReports(id);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<WaterFlavourDto>> getWaterFlavours() {
    return waterService.getFlavours().stream()
        .map(WaterFlavour::toDto)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<List<WaterStateDetail>> getWaterStates(OffsetDateTime from,
                                                               OffsetDateTime to) {
    return waterService.stateHistory(from.toLocalDateTime(), to.toLocalDateTime()).stream()
        .map(WaterState::toDetail)
        .collect(collectingAndThen(toList(), ResponseEntity::ok));
  }

  @Override
  public ResponseEntity<WaterReportDetail> submitWaterReport(WaterReportDto waterReportDto) {
    final var report = waterService.registerReport(waterReportDto);
    return ResponseEntity.ok(report.toDetail());
  }

}
