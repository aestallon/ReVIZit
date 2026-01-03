import {effect, inject, Injectable, OnInit, signal} from '@angular/core';
import {
  WaterFlavourDto,
  WaterReportDetail,
  WaterReportDto,
  WaterService,
  WaterStateDto
} from '../../api/revizit';
import {lastValueFrom} from 'rxjs';
import {MessageService} from 'primeng/api';
import {asErrorMsg} from './errors';
import {UserService} from './user.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

const STATE_UNKNOWN: WaterStateDto = {
  emptyGallons: 0,
  waterLevel: 0,
  fullGallons: 0,
  reportedAt: new Date().toISOString(),
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

  userService = inject(UserService);
  waterApi = inject(WaterService);
  messageService = inject(MessageService);
  userEffect = effect(async () => {
    if (this.userService.profile()) {
      await this.loadPendingReports();
    }
  })

  constructor() {
    this.userService._needStateRefresh.pipe(takeUntilDestroyed()).subscribe(() => this.loadWaterState());
    this.userService._needReportRefresh.pipe(takeUntilDestroyed()).subscribe(() => this.loadPendingReports());
  }

  async loadWaterState() {
    await this.loadWaterFlavours();


    try {
      const state = await lastValueFrom(this.waterApi.getCurrentWaterState());
      this.waterState.set(state);
    } catch (err) {
      if (404 === (err as any)?.status) {
        this.messageService.add(asErrorMsg(err, 'No water state available!'));
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
      this.pendingReportError.set(true);
      this.messageService.add(asErrorMsg(e, 'Failed to load pending reports'));
    }
  }

  async loadWaterFlavours() {
    const res = await lastValueFrom(this.waterApi.getWaterFlavours());
    const map = new Map<number, WaterFlavourDto>(res.map(flavour => [flavour.id, flavour]));
    this.waterFlavours.set(map);
  }

  async rejectReports(report: WaterReportDetail) {
    await lastValueFrom(this.waterApi.rejectWaterReport([report.id]));
    await this.loadPendingReports();
  }

  async acceptReports(reports: Array<WaterReportDetail>) {
    await lastValueFrom(this.waterApi.approveWaterReport(reports.map(report => report.id)));
  }

  async defineState(dto: WaterStateDto) {
    await lastValueFrom(this.waterApi.defineCurrentWaterState(dto));
    this.waterState.set(dto);
  }

  async loadAllWaterFlavours() {
    await Promise.all([
      lastValueFrom(this.waterApi.getWaterFlavours()).then(fs => this.waterFlavours.set(new Map(fs.map(flavour => [flavour.id, flavour])))),
      lastValueFrom(this.waterApi.getAllWaterFlavours()).then(fs => this.allWaterFlavours.set(fs)),
      lastValueFrom(this.waterApi.getInUseWaterFlavours()).then(fs => this.usedWaterFlavours.set(new Set(fs)))
    ]);
  }

  async renameFlavour(flavour: WaterFlavourDto) {
    const _ = await lastValueFrom(this.waterApi.updateWaterFlavour(flavour.id, flavour));
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
