import {inject, Injectable, signal} from '@angular/core';
import {
  WaterFlavourDto,
  WaterReportDetail,
  WaterReportDto,
  WaterService,
  WaterStateDto
} from '../../api/revizit';
import {lastValueFrom, tap} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class RevizitService {

  readonly waterState = signal<WaterStateDto>({
    emptyGallons: 0,
    waterLevel: 0,
    fullGallons: 0,
    reportedAt: '',
  })

  readonly pendingReports = signal<Array<WaterReportDetail>>([]);
  readonly pendingReportError = signal<boolean>(false);
  readonly waterFlavours = signal<Map<number, WaterFlavourDto>>(new Map());

  waterApi = inject(WaterService);

  async loadWaterState() {
    try {
      const state = await lastValueFrom(this.waterApi.getCurrentWaterState());
      this.waterState.set(state);
    } catch (e) {
      console.error('Error on fetching water state: ', e);
    }
  }

  async submitWaterReport(report: WaterReportDto) {
    try {
      const res = await lastValueFrom(this.waterApi.submitWaterReport(report));
      return Promise.resolve(res);
    } catch (e) {
      return Promise.reject(e);
    }
  }

  async loadPendingReports() {
    try {
      const res = await lastValueFrom(this.waterApi.getPendingWaterReports());
      this.pendingReports.set(res);
      this.pendingReportError.set(false);
    } catch (e) {
      console.error('Error on fetching pending reports: ', e);
      this.pendingReportError.set(true);
    }
  }

  async loadWaterFlavours() {
    try {
      const res = await lastValueFrom(this.waterApi.getWaterFlavours());
      const map = new Map<number, WaterFlavourDto>(res.map(flavour => [flavour.id, flavour]));
      this.waterFlavours.set(map);
    } catch (e) {
      console.error('Error on fetching water flavours: ', e);
    }
  }

  async rejectReports(report: WaterReportDetail) {
    try {
      await lastValueFrom(this.waterApi.rejectWaterReport([report.id]));
      await this.loadPendingReports();
    } catch (e) {
      console.error('Error on rejecting reports: ', e);
    }
  }

  async acceptReports(reports: Array<WaterReportDetail>) {
    try {
      await lastValueFrom(this.waterApi.approveWaterReport(reports.map(report => report.id)));
      await this.loadPendingReports();
    } catch (e) {
      console.error('Error on approving reports: ', e);
    }
  }

}
