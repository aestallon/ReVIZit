import {Injectable} from '@angular/core';

export interface WaterState {
  percentage: number;
  timestamp: Date;
  emptyCount: number;
  fullCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class MockService {

  async getCurrentState(): Promise<WaterState> {
    return {
      percentage: 75,
      timestamp: new Date(),
      emptyCount: 4,
      fullCount: 3
    };
  }
}
