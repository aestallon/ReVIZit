package org.revizit.rest.impl;

import java.time.OffsetDateTime;
import java.util.List;
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
    return WaterApiDelegate.super.getPendingWaterReports();
  }

  @Override
  public ResponseEntity<Void> approveWaterReport(List<Long> id) {
    return WaterApiDelegate.super.approveWaterReport(id);
  }

  @Override
  public ResponseEntity<Void> rejectWaterReport(List<Long> id) {
    return WaterApiDelegate.super.rejectWaterReport(id);
  }

  @Override
  public ResponseEntity<List<WaterFlavourDto>> getWaterFlavours() {
    return WaterApiDelegate.super.getWaterFlavours();
  }

  @Override
  public ResponseEntity<List<WaterStateDetail>> getWaterStates(OffsetDateTime from,
                                                               OffsetDateTime to) {
    return WaterApiDelegate.super.getWaterStates(from, to);
  }

  @Override
  public ResponseEntity<WaterReportDetail> submitWaterReport(WaterReportDto waterReportDto) {
    final var report = waterService.registerAndAccept(waterReportDto);
    return ResponseEntity.ok(report.toDetail());
  }

}
