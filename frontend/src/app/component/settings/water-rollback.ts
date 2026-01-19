import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {WaterStateDetail} from '../../../api/revizit';
import {RevizitService} from '../../service/revizit.service';
import {MessageService, PrimeIcons, PrimeTemplate} from 'primeng/api';
import {asErrorMsg} from '../../service/errors';
import {Button} from 'primeng/button';
import {DatePipe, NgClass} from '@angular/common';
import {TableModule} from 'primeng/table';
import {WaterGallonComponent} from '../gallon';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-water-rollback',
  template: `
    <div class="rollback-container">
      <p-table [value]="stateHistory()"
               dataKey="id"
               [rowHover]="true"
               [loading]="loading()" class="reports-table">
        <ng-template pTemplate="caption">
          <h4>Rollback State</h4>
          <p-message severity="warn" [icon]="PrimeIcons.INFO_CIRCLE">
            <p>You may roll back the application state here.</p>
          </p-message>
        </ng-template>
        <ng-template pTemplate="header">
          <tr>
            <th>Full</th>
            <th>Empty</th>
            <th>Water Level</th>
            <th>Timestamp</th>
            <th>Actions</th>
          </tr>
        </ng-template>

        <ng-template pTemplate="body"
                     let-state
                     let-rowIndex="rowIndex">
          <tr [ngClass]="rowClasses(state, rowIndex)"
              (mouseleave)="clearHover()">
            <td>{{ state.waterState.fullGallons }} x
              <app-water-gallon [waterLevel]="100" [editable]="false"
                                [flipped]="true"></app-water-gallon>
            </td>
            <td><span>{{ state.waterState.emptyGallons }} x </span>
              <app-water-gallon [waterLevel]="0" [editable]="false"
                                [flipped]="true"></app-water-gallon>
            </td>
            <td>{{ state.waterState.waterLevel }}%
              <app-water-gallon [waterLevel]="state.waterState.waterLevel"
                                [editable]="false"></app-water-gallon>
            </td>
            <td>{{ state.waterState.reportedAt | date:'medium' }}</td>
            <td class="row-controls">
              @if (!isAfterRollbackRow(rowIndex)) {
                <p-button label="ROLLBACK"
                          severity="danger"
                          [icon]="PrimeIcons.TIMES_CIRCLE"
                          [disabled]="loading() || unavailable()"
                          [loading]="loading()"
                          (mouseenter)="hoverRollback(state)"
                          (onClick)="performRollback(state)">
                </p-button>
              }
              @if (isAfterRollbackRow(rowIndex)) {
                <div class="rollback-popup">
                  State after rollback
                </div>
              }
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
    NgClass,
    WaterGallonComponent,
    Message
  ],
  styles: `
    .row-rollback-hover {
      background-color: rgba(129, 108, 107, 0.25) !important; /* orange */
    }

    app-water-gallon {
      height: 2rem;
    }
    td {
      align-content: center;
    }

    .row-target-hover {
      position: relative;
      animation: golden-pulse 1.8s infinite ease-in-out;
      box-shadow: 0 0 8px rgba(255, 215, 0, 0.8),
      0 0 16px rgba(255, 215, 0, 0.6),
      inset 0 0 6px rgba(255, 215, 0, 0.5);
      border-left: 4px solid gold;
      background-color: rgba(255, 215, 0, 0.12);
    }

    /* Golden glow animation */
    @keyframes golden-pulse {
      0% {
        box-shadow: 0 0 6px rgba(255, 215, 0, 0.6);
      }
      50% {
        box-shadow: 0 0 18px rgba(255, 215, 0, 1);
      }
      100% {
        box-shadow: 0 0 6px rgba(255, 215, 0, 0.6);
      }
    }

    .rollback-cell {
      position: relative;
    }

    .rollback-popup {
      position: absolute;
      bottom: 110%;
      left: 50%;
      transform: translateX(-50%);
      background: linear-gradient(145deg, #3a2b00, #1c1400);
      color: gold;
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 0.85rem;
      white-space: nowrap;
      box-shadow:
        0 0 12px rgba(255, 215, 0, 0.9),
        0 0 24px rgba(255, 215, 0, 0.5);
      z-index: 1000;
      pointer-events: none;
    }

    /* Arrow */
    .rollback-popup::after {
      content: '';
      position: absolute;
      top: 100%;
      left: 50%;
      transform: translateX(-50%);
      border-width: 6px;
      border-style: solid;
      border-color: gold transparent transparent transparent;
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
      } else if (rowIndex === rollbackIdx + 1) {
        return {'row-target-hover': true};
      }
    }

    return {};
  }

  isAfterRollbackRow(rowIndex: number): boolean {
    const rollbackId = this.hoverRollbackId();
    if (rollbackId === null) return false;

    const rollbackIdx = this.stateHistory()
      .findIndex(r => r.id === rollbackId);

    return rowIndex === rollbackIdx + 1;
  }

  protected readonly PrimeIcons = PrimeIcons;
}
