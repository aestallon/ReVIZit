import {AfterViewInit, Component, computed, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {Panel} from 'primeng/panel';
import {DatePicker} from 'primeng/datepicker';
import {FormsModule} from '@angular/forms';
import {RevizitService} from '../service/revizit.service';
import {MessageService, PrimeIcons} from 'primeng/api';
import {asErrorMsg} from '../service/errors';
import {Button} from 'primeng/button';
import {UIChart} from 'primeng/chart';
import {FloatLabel} from 'primeng/floatlabel';
import {ChartOptions} from 'chart.js';
import 'chartjs-adapter-date-fns';

const createReporterOptions = (): ChartOptions => {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue('--text-color');
  return {
    plugins: {
      legend: {
        labels: {
          usePointStyle: true,
          color: textColor,
        }
      }
    }
  };
};


@Component({
  selector: 'app-stats',
  template: `
    <p-card header="Statistics">
      <p-panel header="Select period">
        <div class="interval-selection-form">
          <p-float-label>
            <label for="on_intervalSelectorFrom">From</label>
            <p-date-picker id="intervalSelectorFrom"
                           placeholder="From..."
                           [(ngModel)]="from"
                           dateFormat="d MM, yy"
                           [showClear]="true"
                           [disabled]="loading() || unavailable()">
            </p-date-picker>
          </p-float-label>
          <p-float-label>
            <label for="on_intervalSelectorTo">Until</label>
            <p-date-picker id="intervalSelectorTo"
                           placeholder="Until..."
                           [(ngModel)]="to"
                           dateFormat="d MM, yy"
                           [showClear]="true"
                           [disabled]="loading() || unavailable()">
            </p-date-picker>
          </p-float-label>
          <p-button severity="primary"
                    label="Fetch"
                    [icon]="PrimeIcons.REFRESH"
                    [loading]="loading()"
                    [disabled]="unavailable()"
                    (onClick)="fetchWaterStates()">
          </p-button>
        </div>
      </p-panel>
      <p-panel header="Water State History">
        <p-chart type="line"
                 [data]="waterLevelData()"
                 [options]="waterLevelOptions()"
                 class="chart">
        </p-chart>
      </p-panel>
      <p-panel header="Reporters">
        <p-chart type="pie" [data]="reporterData()"
                 [options]="reporterOptions"
                 class="chart">
        </p-chart>
      </p-panel>

    </p-card>
  `,
  imports: [
    Card,
    Panel,
    DatePicker,
    FormsModule,
    Button,
    UIChart,
    FloatLabel
  ],
  styles: `
    .interval-selection-form {
      display: flex;
      gap: 2rem;
      padding-top: 2rem;
    }

    .chart {
      height: 400px;
    }

    :host ::ng-deep .p-panel {
      margin-bottom: 0.5rem;
    }
  `
})
export class Statistics implements AfterViewInit {

  service = inject(RevizitService);
  msg = inject(MessageService);

  from = signal<Date | undefined>(undefined);
  to = signal<Date | undefined>(undefined);

  stateHistory = computed(() => this.service.stateHistory());
  waterLevelData = computed(() => {
    const states = this.stateHistory();
    if (states.length === 0) {
      return {};
    }

    const remainingWater = states.map(it => {
      return {
        x: it.waterState.reportedAt,
        y: it.waterState.waterLevel + it.waterState.fullGallons * 100
      };
    });
    const waterLevel = states.map(it => ({
      x: it.waterState.reportedAt,
      y: it.waterState.waterLevel
    }));
    return {
      datasets: [
        {
          type: 'line',
          data: remainingWater,
          label: 'Remaining water',
          yAxisID: 'y',
          tension: 0
        },
        {
          type: 'line',
          data: waterLevel,
          label: 'Water level',
          yAxisID: 'y1',
        }
      ],
    };
  });

  waterLevelOptions = computed(() => {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--p-text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color');
    const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color');
    const states = this.stateHistory();
    if (states.length === 0) {
      return {};
    }

    return {
      stacked: false,
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            color: textColor,
          }
        }
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: 'day',
            hour: 'd MMM HH:mm'
          },
          min: states[0].waterState.reportedAt,
          max: states.at(-1)!.waterState.reportedAt,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
          }
        },
        y: {
          id: 'y',
          position: 'left',
          min: 0,
          max: states[0].waterState.fullGallons * 100 + states[0].waterState.emptyGallons * 100 + 100,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
          }
        },
        y1: {
          id: 'y1',
          position: 'right',
          min: 0,
          max: 100,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            drawOnChartArea: false,
            color: surfaceBorder,
          }
        }

      }
    };
  });

  reporterData = computed(() => {
    const states = this.stateHistory();
    if (states.length === 0) {
      return {};
    }

    const reportsByUser: Record<string, number> = {};
    for (const state of states) {
      const user = state.reportedBy ?? 'anonymous';
      reportsByUser[user] = (reportsByUser[user] ?? 0) + 1;
    }
    const labels = [];
    const data = [];
    for (const [user, count] of Object.entries(reportsByUser)) {
      labels.push(user);
      data.push(count);
    }

    return {
      labels,
      datasets: [{data}]
    };
  });

  reporterOptions = createReporterOptions();

  loading = signal<boolean>(true);
  unavailable = signal<boolean>(false);

  fetchWaterStates(setUnavailable?: boolean) {
    this.loading.set(true);
    this.service.fetchWaterStates({from: this.from(), to: this.to()})
      .then(() => {
      })
      .catch((err) => {
        if (setUnavailable) this.unavailable.set(true);
        this.msg.add(asErrorMsg(err, 'Failed to fetch water states'));
      })
      .finally(() => this.loading.set(false));
  }

  ngAfterViewInit() {
    this.fetchWaterStates(true);
  }

  protected readonly PrimeIcons = PrimeIcons;
}
