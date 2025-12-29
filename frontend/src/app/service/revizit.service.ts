import {inject, Injectable, signal} from '@angular/core';
import {
  WaterFlavourDto,
  WaterReportDetail,
  WaterReportDto,
  WaterService,
  WaterStateDto
} from '../../api/revizit';
import {lastValueFrom, tap} from 'rxjs';
import {MessageService} from 'primeng/api';
import {asCustomErrorMsg} from './errors';

const STATE_UNKNOWN: WaterStateDto = {
  emptyGallons: 0,
  waterLevel: 0,
  fullGallons: 0,
  reportedAt: '',
  flavour: {
    id: 0,
    name: '<<Unknown>>',
    inactive: false,
  }
};

@Injectable({
  providedIn: 'root'
})
export class RevizitService {


  readonly waterState = signal<WaterStateDto>(STATE_UNKNOWN);

  readonly pendingReports = signal<Array<WaterReportDetail>>([]);
  readonly pendingReportError = signal<boolean>(false);
  readonly waterFlavours = signal<Map<number, WaterFlavourDto>>(new Map());

  readonly allWaterFlavours = signal<Array<WaterFlavourDto>>([]);
  readonly usedWaterFlavours = signal<Set<number>>(new Set());

  waterApi = inject(WaterService);
  messageService = inject(MessageService);

  async loadWaterState() {
    await this.loadWaterFlavours();


    try {
      const state = await lastValueFrom(this.waterApi.getCurrentWaterState());
      this.waterState.set(state);
    } catch (err) {
      if (404 === (err as any)?.status) {
        this.messageService.add(asCustomErrorMsg(err, 'No water state available!'));
        this.waterState.set(STATE_UNKNOWN);
      } else {
        throw err;
      }
    }
  }

  async submitWaterReport(report: WaterReportDto) {
    return await lastValueFrom(this.waterApi.submitWaterReport(report));
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
    await lastValueFrom(this.waterApi.approveWaterReport(reports.map(report => report.id)));
  }

  async defineState(dto: WaterStateDto) {
    try {
      await lastValueFrom(this.waterApi.defineCurrentWaterState(dto));
      this.waterState.set(dto);
      return true;
    } catch (e) {
      throw e;
    }
  }

  async loadAllWaterFlavours() {
    await Promise.all([
      lastValueFrom(this.waterApi.getWaterFlavours()).then(fs => this.waterFlavours.set(new Map(fs.map(flavour => [flavour.id, flavour])))),
      lastValueFrom(this.waterApi.getAllWaterFlavours()).then(fs => this.allWaterFlavours.set(fs)),
      lastValueFrom(this.waterApi.getInUseWaterFlavours()).then(fs => this.usedWaterFlavours.set(new Set(fs)))
    ]);
  }

  async renameFlavour(flavour: WaterFlavourDto) {
    const _flavour = await lastValueFrom(this.waterApi.updateWaterFlavour(flavour.id, flavour));
    await this.loadAllWaterFlavours();
  }

  async createFlavour(name: string) {
    const flavour = await lastValueFrom(this.waterApi.createWaterFlavour(name));
    await this.loadAllWaterFlavours();
    return flavour;
  }

  async deleteFlavour(flavour: WaterFlavourDto) {
    await lastValueFrom(this.waterApi.deleteWaterFlavour(flavour.id));
    await this.loadAllWaterFlavours();
  }

  async activateFlavour(flavour: WaterFlavourDto) {
    await lastValueFrom(this.waterApi.activateWaterFlavour(flavour.id));
    await this.loadAllWaterFlavours();
  }

}
