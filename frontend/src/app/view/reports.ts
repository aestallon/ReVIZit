import {Component, computed, inject, signal} from '@angular/core';
import {RevizitService} from '../service/revizit.service';
import {UserService} from '../service/user.service';
import {TableModule} from 'primeng/table';
import {WaterReportDetail, WaterReportDto, WaterReportKind} from '../../api/revizit';
import {DatePipe, NgClass} from '@angular/common';
import {Button} from 'primeng/button';
import {PrimeIcons} from 'primeng/api';
import {Card} from 'primeng/card';

@Component({
  selector: 'app-reports',
  template: `
    <div class="reports-container">
      <div class="reports-header">
        <h1>Pending Reports</h1>
        <div class="reports-description">
          <p>The table below shows the currently pending reports in chronological order.</p>
          <p>You may reject any of them by clicking the <i
            class="pi pi-times-circle"></i><b>Reject</b> button for the corresponding row, or accept
            them by clicking <i class="pi pi-check-circle"></i><b>Accept</b>.</p>
          <p><strong>When you accept a report, you automatically accept all other older reports
            too!</strong></p>
        </div>
      </div>
      <p-card class="reports-table">
        <p-table [value]="pendingReports()"
                 dataKey="id"
                 [rowHover]="true"
                 [loading]="loading()" class="reports-table">
          <ng-template pTemplate="header">
            <tr>
              <th>Details</th>
              <th>Reported By</th>
              <th>Reported At</th>
              <th>Actions</th>
            </tr>
          </ng-template>

          <ng-template pTemplate="body"
                       let-report
                       let-rowIndex="rowIndex">
            <tr [ngClass]="rowClasses(report, rowIndex)"
                (mouseleave)="clearHover()">
              <td [ngClass]="descriptionClasses(report)">{{ reportDescription(report) }}</td>
              <td>{{ report.reportedBy || 'anonymous' }}</td>
              <td>{{ report.reportedAt | date:'medium' }}</td>
              <td class="row-controls">
                <p-button label="Reject"
                          severity="warn"
                          [icon]="PrimeIcons.TIMES_CIRCLE"
                          [disabled]="loading()"
                          (mouseenter)="hoverReject(report)"
                          (onClick)="performReject(report)">
                </p-button>
                <p-button label="Accept"
                          severity="primary"
                          [icon]="PrimeIcons.CHECK_CIRCLE"
                          [disabled]="loading()"
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
      </p-card>

    </div>
  `,
  imports: [
    TableModule,
    DatePipe,
    Button,
    NgClass,
    Card
  ],
  styles: `
    .reports-container {
      display: flex;
      flex-direction: column;
    }

    .reports-header {
      display: flex;
      flex-direction: column;
    }

    .reports-description {
      align-self: center;
    }

    :host ::ng-deep .reports-table {
      align-self: center;
      flex: 1

    }

    .row-accept-hover {
      background-color: rgba(40, 167, 69, 0.15) !important; /* green */
    }

    .row-reject-hover {
      background-color: rgba(255, 193, 7, 0.25) !important; /* orange */
    }

    .row-controls {
      display: flex;
      gap: 0.5rem;
    }

    .unique-report {
      font-weight: 800;
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
        return `New gallon (${this.service.waterFlavours().get(wr.flavourId!)?.name ?? 'Unknown'} flavour)`;
      case WaterReportKind.REFILL:
        return 'Empty gallons refilled';
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

  descriptionClasses(report: WaterReportDetail) {
    return WaterReportKind.PERCENTAGE !== report.waterReport.kind ? {'unique-report': true} : {};
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
