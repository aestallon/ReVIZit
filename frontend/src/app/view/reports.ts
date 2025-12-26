import {Component, computed, inject, signal} from '@angular/core';
import {RevizitService} from '../service/revizit.service';
import {UserService} from '../service/user.service';
import {TableModule} from 'primeng/table';
import {WaterReportDetail, WaterReportDto, WaterReportKind} from '../../api/revizit';
import {DatePipe, NgClass} from '@angular/common';
import {Button} from 'primeng/button';
import {PrimeIcons} from 'primeng/api';

@Component({
  selector: 'app-reports',
  template: `
    <div class="reports-container">
      <div class="reports-header">
        <h1>Pending Reports</h1>
      </div>
      <div class="reports-controls">

      </div>
      <p-table [value]="pendingReports()"
               dataKey="id"
               [rowHover]="true"
               [loading]="loading()">
        <ng-template pTemplate="header">
          <tr>
            <th>Reported At</th>
            <th>Reported By</th>
            <th>Details</th>
            <th>Actions</th>
          </tr>
        </ng-template>

        <ng-template pTemplate="body"
                     let-report
                     let-rowIndex="rowIndex">
          <tr [ngClass]="rowClasses(report, rowIndex)"
              (mouseleave)="clearHover()">
            <td>{{ report.reportedAt | date:'medium' }}</td>
            <td>{{ report.reportedBy || 'anonymous' }}</td>
            <td>{{ reportDescription(report) }}</td>
            <td>
              <p-button label="Reject"
                        severity="warn"
                        [icon]="PrimeIcons.TIMES_CIRCLE"
                        (mouseenter)="hoverReject(report)"
                        (onClick)="performReject(report)">
              </p-button>
              <p-button label="Accept"
                        severity="primary"
                        [icon]="PrimeIcons.CHECK_CIRCLE"
                        (mouseenter)="hoverAccept(report)"
                        (onClick)="performAccept(report)">
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
    TableModule,
    DatePipe,
    Button,
    NgClass
  ],
  styles: `
    .reports-container {
      display: flex;
      flex-direction: column;
      padding: 0em 10em;
    }

    .row-accept-hover {
      background-color: rgba(40, 167, 69, 0.15) !important; /* green */
    }

    .row-reject-hover {
      background-color: rgba(255, 193, 7, 0.25) !important; /* orange */
    }
  `
})
export class Reports {

  userService = inject(UserService);
  service = inject(RevizitService);

  readonly pendingReports = computed(() => this.service.pendingReports());

  readonly hoveredAcceptId = signal<number | null>(null);
  readonly hoveredRejectId = signal<number | null>(null);
  readonly loading = signal(false);

  constructor() {
    this.loading.set(true);
    Promise.all([this.service.loadPendingReports(), this.service.loadWaterFlavours()])
      .then(() => this.loading.set(false))
      .catch(err => this.loading.set(false));
  }

  reportDescription(report: WaterReportDetail): string {
    const wr = report.waterReport;

    switch (wr.kind) {
      case WaterReportKind.PERCENTAGE:
        return `Percentage: ${wr.value ?? '—'}%`;
      case WaterReportKind.SWAP:
        return `Swap (${this.service.waterFlavours().get(wr.flavourId!)?.name ?? 'Unknown'} flavour)`;
      case WaterReportKind.REFILL:
        return 'Refill';
      default:
        return '';
    }
  }

  hoverAccept(report: WaterReportDetail) {
    this.hoveredAcceptId.set(report.id);
    this.hoveredRejectId.set(null);
  }

  hoverReject(report: WaterReportDetail) {
    this.hoveredRejectId.set(report.id);
    this.hoveredAcceptId.set(null);
  }

  clearHover() {
    this.hoveredAcceptId.set(null);
    this.hoveredRejectId.set(null);
  }

  rowClasses(report: WaterReportDetail, rowIndex: number) {
    const acceptId = this.hoveredAcceptId();
    const rejectId = this.hoveredRejectId();

    // Reject → only the hovered row
    if (rejectId === report.id) {
      return {'row-reject-hover': true};
    }

    // Accept → all rows up to and including hovered row
    if (acceptId !== null) {
      const acceptIndex = this.pendingReports()
        .findIndex(r => r.id === acceptId);

      if (rowIndex <= acceptIndex) {
        return {'row-accept-hover': true};
      }
    }

    return {};
  }

  performReject(dto: WaterReportDetail) {
    this.loading.set(true);
    this.service.rejectReports(dto).then(() => this.loading.set(false));
  }

  performAccept(dto: WaterReportDetail) {
    this.loading.set(true);

    const reportsToSubmit = [];
    let reportFound = false;
    for (const report of this.pendingReports()) {
      reportsToSubmit.push(report);
      if (report.id === dto.id) {
        reportFound = true;
        break;
      }
    }

    if (!reportFound) {
      console.error('Could not find report to accept!');
      return;
    }

    this.service.acceptReports(reportsToSubmit).then(() => this.loading.set(false));
  }

  protected readonly PrimeIcons = PrimeIcons;
}
