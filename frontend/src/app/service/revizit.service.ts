import {inject, Injectable, signal} from '@angular/core';
import {WaterReportDto, WaterService, WaterStateDto} from '../../api/revizit';
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

}
