import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {WaterReportDetail, WaterStateDetail} from '../../../api/revizit';
import {RevizitService} from '../../service/revizit.service';
import {MessageService, PrimeIcons, PrimeTemplate} from 'primeng/api';
import {lastValueFrom} from 'rxjs';
import {asErrorMsg} from '../../service/errors';
import {Button} from 'primeng/button';
import {DatePipe, NgClass} from '@angular/common';
import {TableModule} from 'primeng/table';
import {UserCard} from '../user.card';

@Component({
  selector: 'app-water-rollback',
  template: `
    <div class="rollback-container">
      <p-table [value]="stateHistory()"
               dataKey="id"
               [rowHover]="true"
               [loading]="loading()" class="reports-table">
        <ng-template pTemplate="header">
          <tr>
            <th>Full</th>
            <th>Empty</th>
            <th>Current %</th>
            <th>Timestamp</th>
            <th>Actions</th>
          </tr>
        </ng-template>

        <ng-template pTemplate="body"
                     let-state
                     let-rowIndex="rowIndex">
          <tr [ngClass]="rowClasses(state, rowIndex)"
              (mouseleave)="clearHover()">
            <td>{{ state.waterState.fullGallons }}</td>
            <td>{{ state.waterState.emptyGallons }}</td>
            <td>{{ state.waterState.waterLevel }}</td>
            <td>{{ state.waterState.reportedAt | date:'medium' }}</td>
            <td class="row-controls">
              <p-button label="ROLLBACK"
                        severity="danger"
                        [icon]="PrimeIcons.TIMES_CIRCLE"
                        [disabled]="loading()"
                        (mouseenter)="hoverRollback(state)"
                        (onClick)="performRollback(state)">
              </p-button>
            </td>
          </tr>
        </ng-template>

        <ng-template pTemplate="emptymessage">
          <tr>
            <td
              colspan="4">{{ service.pendingReportError() ? 'Error loading pending reports' : 'No pending reports' }}
            </td>
          </tr>
        </ng-template>
      </p-table>
    </div>
  `,
  imports: [
    Button,
    DatePipe,
    PrimeTemplate,
    TableModule,
    UserCard,
    NgClass
  ],
  styles: `
    .row-rollback-hover {
      background-color: rgba(255, 36, 7, 0.25) !important; /* orange */
    }
  `
})
export class WaterRollback implements OnInit {

  readonly loading = signal(false);
  readonly unavailable = signal(false);

  readonly service = inject(RevizitService);
  readonly msg = inject(MessageService);

  readonly hoverRollbackId = signal<number | null>(null);
  readonly stateHistory = computed<Array<WaterStateDetail>>(() => [...this.service.stateHistory()].reverse());

  ngOnInit() {
    this.fetchWaterStateHistory(true);
  }

  fetchWaterStateHistory(setUnavailable?: boolean) {
    this.loading.set(true)
    this.service.fetchWaterStates({})
      .then(() => {
      })
      .catch(err => {
        this.msg.add(asErrorMsg(err, 'Failed to fetch water state history'));
        if (setUnavailable) this.unavailable.set(true);
      })
      .finally(() => this.loading.set(false));
  }

  performRollback(state: WaterStateDetail) {
    this.loading.set(true);
    this.service.rollbackState(state)
      .catch(err => this.msg.add(asErrorMsg(err, 'Failed to rollback to state')))
      .then(() => this.fetchWaterStateHistory())
      .finally(() => this.loading.set(false));
  }

  hoverRollback(state: WaterStateDetail) {
    this.hoverRollbackId.set(state.id);
  }

  clearHover() {
    this.hoverRollbackId.set(null);
  }

  rowClasses(state: WaterStateDetail, rowIndex: number) {
    const rollbackId = this.hoverRollbackId();

    // Accept → all rows up to and including hovered row
    if (rollbackId !== null) {
      const rollbackIdx = this.stateHistory()
        .findIndex(r => r.id === rollbackId);

      if (rowIndex <= rollbackIdx) {
        return {'row-rollback-hover': true};
      }
    }

    return {};
  }

  protected readonly PrimeIcons = PrimeIcons;
}
