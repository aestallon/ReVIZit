import {Component, input, model, output} from '@angular/core';
import {Button} from 'primeng/button';
import {DatePicker} from 'primeng/datepicker';
import {FloatLabel} from 'primeng/floatlabel';
import {PrimeIcons} from 'primeng/api';
import {FormsModule} from '@angular/forms';
import {Divider} from 'primeng/divider';

@Component({
  selector: 'app-interval-selector',
  template: `
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
      <p-divider layout="vertical"></p-divider>
      <p-button severity="primary"
                label="Fetch"
                [icon]="PrimeIcons.REFRESH"
                [loading]="loading()"
                [disabled]="unavailable()"
                (onClick)="onFetch.emit($event)">
      </p-button>
    </div>
  `,
  styles: `
    .interval-selection-form {
      display: flex;
      gap: 0.5rem;
      padding-top: 2rem;
    }
  `,
  imports: [
    Button,
    DatePicker,
    FloatLabel,
    FormsModule,
    Divider
  ]
})
export class IntervalSelector {

  from = model.required<Date | undefined>();
  to = model.required<Date | undefined>();
  onFetch = output<MouseEvent>();
  loading = input.required<boolean>();
  unavailable = input.required<boolean>();
  protected readonly PrimeIcons = PrimeIcons;
}
