import {Component, computed, inject, signal} from '@angular/core';
import {Step, StepList, StepPanel, StepPanels, Stepper} from 'primeng/stepper';
import {Card} from 'primeng/card';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {SelectButton} from 'primeng/selectbutton';
import {Button} from 'primeng/button';
import {MessageService, PrimeIcons} from 'primeng/api';
import {WaterGallonComponent} from '../component/gallon';
import {InputNumber} from 'primeng/inputnumber';
import {Router} from '@angular/router';


@Component({
  selector: 'app-create-report',
  template: `
    <p-stepper [value]="1" [linear]="true" class="report-container">
      <p-step-list>
        <p-step [value]="1">Report Type</p-step>
        <p-step [value]="2">Submit</p-step>
      </p-step-list>
      <p-step-panels>
        <p-step-panel [value]="1">
          <ng-template #content let-activateCallback="activateCallback">
            <p-card header="What kind of report would you like to make?">
              <div class="type-card">
                <p-select-button [options]="typeFormOptions" class="type-select"
                                 [formControl]="type"
                                 optionLabel="label"
                                 optionValue="value"
                                 [invalid]="type.invalid">
                </p-select-button>
                <div>{{ typeDescription() }}</div>
                <p-button [disabled]="!type.value || type.value.length < 0"
                          label="Next"
                          severity="primary"
                          class="next-btn"
                          [icon]="PrimeIcons.ARROW_RIGHT"
                          (onClick)="activateCallback(2)">
                </p-button>
              </div>
            </p-card>
          </ng-template>
        </p-step-panel>
        <p-step-panel [value]="2">
          <ng-template #content let-activateCallback="activateCallback">
            <p-card [header]="confirmTitle()">
              <div class="type-card">
                @if (selectedType() === 'waterUsage') {
                  <app-water-gallon [(waterLevel)]="waterLevel"
                                    [editable]="true"></app-water-gallon>
                  <div>
                    Select a percentage by clicking on the picture above, or manually entering the
                    value below:
                  </div>
                  <p-input-number [(ngModel)]="waterLevel"
                                  inputId="minmax"
                                  mode="decimal"
                                  suffix="%"
                                  [invalid]="waterLevel() < 0 || waterLevel() > 100"
                                  [min]="0"
                                  [max]="100">
                  </p-input-number>
                } @else if (selectedType() === 'gallonSwap') {
                  <div><b>A new gallon has been inserted into the dispenser.</b></div>
                } @else if (selectedType() === 'gallonRefill') {
                  <div><b>All empty gallons have been refilled.</b></div>
                } @else {
                  ERROR
                }
                <div class="confirm-controls">
                  <p-button label="Back"
                            severity="secondary"
                            [icon]="PrimeIcons.ARROW_LEFT"
                            (onClick)="activateCallback(1)">
                  </p-button>
                  <p-button label="Submit"
                            severity="primary"
                            class="next-btn"
                            [disabled]="waterLevel() < 0 || waterLevel() > 100"
                            [icon]="PrimeIcons.ARROW_CIRCLE_RIGHT"
                            (onClick)="submitReport()">
                  </p-button>
                </div>
              </div>
            </p-card>
          </ng-template>
        </p-step-panel>
      </p-step-panels>
    </p-stepper>
  `,
  imports: [
    Stepper,
    StepList,
    Step,
    StepPanels,
    StepPanel,
    Card,
    ReactiveFormsModule,
    SelectButton,
    Button,
    WaterGallonComponent,
    InputNumber,
    FormsModule
  ],
  styles: `
    :host {
      display: flex;
      justify-content: center;
      align-self: center;
    }

    .report-container {
      width: 600px;
    }

    .type-card {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .type-select {
      align-self: center;
    }

    .next-btn {
      align-self: flex-end;
    }

    .confirm-controls {
      display: flex;
      justify-content: space-between;
    }
  `
})
export class CreateReport {

  type = new FormControl('', Validators.required);
  typeFormOptions: { label: string, value: string }[] = [
    {label: 'Percentage', value: 'waterUsage'},
    {label: 'Swap', value: 'gallonSwap'},
    {label: 'Refill', value: 'gallonRefill'},
  ];

  selectedType = signal<string | null>(null);

  typeDescription = computed<string | null>(() => {
    switch (this.selectedType()) {
      case 'waterUsage':
        return 'Report how much water is present in the current gallon.';
      case 'gallonSwap':
        return 'Report a swap: an empty ballon removed and replaced with a full one.';
      case 'gallonRefill':
        return 'All empty gallons have been refilled.';
      default:
        return null;
    }
  });

  confirmTitle = computed<string>(() => {
    switch (this.selectedType()) {
      case 'waterUsage':
        return 'How Much Water Is in the Gallon?';
      default:
        return 'Confirm Your Report';
    }
  });

  // TODO: Fetch from service!
  waterLevel = signal(0);

  messageService = inject(MessageService);
  router = inject(Router);

  constructor(private fb: FormBuilder) {
    this.type.valueChanges.subscribe(value => {
      this.selectedType.set(value);
    });
  }

  protected readonly PrimeIcons = PrimeIcons;

  submitReport() {
    console.log(this.selectedType(), this.waterLevel());
    this.messageService.add({severity: 'success', summary: 'Success', detail: 'Report submitted successfully!', life: 3000});
    this.router.navigateByUrl('/home');
  }
}
