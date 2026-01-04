import {AfterViewInit, Component, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {Panel} from 'primeng/panel';
import {IntervalSelector} from '../component/interval-selector';
import {RevizitService} from '../service/revizit.service';
import {MessageService} from 'primeng/api';
import {DataView} from 'primeng/dataview';
import {SysLogEntry, SysLogService} from '../../api/revizit';
import {lastValueFrom} from 'rxjs';
import {asErrorMsg} from '../service/errors';
import {LogCard} from '../component/systemlogs/log-card';

// FIXME: This is a hack..
const today = (): Date => {
  const d = new Date();
  d.setTime(d.getTime() + (2 * 60 * 60 * 1000));
  return d;
};
const yesterday = (): Date => {
  const d = today();
  d.setDate(d.getDate() - 1);
  return d;
}

@Component({
  selector: 'app-system-logs',
  template: `
    <p-card header="System Logs">
      <p-panel header="Select period">
        <app-interval-selector [(from)]="from"
                               [(to)]="to"
                               [loading]="loading()"
                               [unavailable]="unavailable()"
                               (onFetch)="fetchLogs()">
        </app-interval-selector>
      </p-panel>
      <p-data-view layout="list" [value]="logs()">
        <ng-template #list let-logs>
          @for (log of logs; track log.timestamp) {
            <app-log-card [log]="log"></app-log-card>
          }
        </ng-template>
      </p-data-view>
    </p-card>
  `,
  styles: `
    :host ::ng-deep .p-panel {
      margin-bottom: 0.5rem;
    }
  `,
  imports: [
    Card,
    Panel,
    IntervalSelector,
    DataView,
    LogCard
  ]
})
export class SystemLogs implements AfterViewInit {

  service = inject(RevizitService);
  msg = inject(MessageService);
  logService = inject(SysLogService);

  from = signal<Date | undefined>(undefined);
  to = signal<Date | undefined>(undefined);

  loading = signal<boolean>(true);
  unavailable = signal<boolean>(false);

  logs = signal<SysLogEntry[]>([]);

  fetchLogs(setUnavailable?: boolean) {
    const from = this.from() ?? yesterday();
    const to = this.to() ?? today();
    this.loading.set(true);
    lastValueFrom(this.logService.getSysLogs(from.toISOString(), to.toISOString()))
      .then(logs => {
        this.logs.set(logs);
      })
      .catch((err) => {
        if (setUnavailable) this.unavailable.set(true);
        this.msg.add(asErrorMsg(err, 'Failed to fetch system logs'));
      })
      .finally(() => this.loading.set(false));
  }

  ngAfterViewInit() {
    this.fetchLogs(true);
  }
}
