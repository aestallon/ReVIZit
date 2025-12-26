import {Component, computed, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {
  FormBuilder,
  FormControl,
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
import {RevizitService} from '../service/revizit.service';
import {WaterReportKind} from '../../api/revizit';
import {Select} from 'primeng/select';


@Component({
  selector: 'app-create-report',
  template: `
    <p-card header="What kind of report would you like to make?">
      <div class="type-card">
        <p-select-button [options]="typeFormOptions" class="type-select"
                         [formControl]="type"
                         optionLabel="label"
                         optionValue="value"
                         [invalid]="type.invalid">
        </p-select-button>
        <div>{{ typeDescription() }}</div>
        @if (selectedType() === WaterReportKind.PERCENTAGE) {
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
        } @else if (selectedType() === WaterReportKind.SWAP) {
          <div>
            Choose a flavour to replace the empty gallon with:
          </div>
          <p-select [options]="flavours()"
                    [(ngModel)]="selectedFlavour"
                    optionLabel="name"
                    optionValue="id"></p-select>
        } @else if (selectedType() === WaterReportKind.REFILL) {
          <div><b>All empty gallons have been refilled.</b></div>
        }
        <div class="confirm-controls">
          <p-button label="Submit"
                    severity="primary"
                    class="next-btn"
                    [loading]="submitting()"
                    [disabled]="submitDisabled()"
                    [icon]="PrimeIcons.ARROW_CIRCLE_RIGHT"
                    (onClick)="submitReport()">
          </p-button>
        </div>
      </div>
    </p-card>
  `,
  imports: [
    Card,
    ReactiveFormsModule,
    SelectButton,
    Button,
    WaterGallonComponent,
    InputNumber,
    FormsModule,
    Select
  ],
  styles: `
    :host {
      display: flex;
      justify-content: center;
      align-self: center;
    }


    .type-card {
      display: flex;
      flex-direction: column;
      gap: 1rem;
      width: 600px;
    }

    .type-select {
      align-self: center;
    }

    .next-btn {
      align-self: flex-end;
    }

    .confirm-controls {
      display: flex;
      justify-content: flex-end;
    }
  `
})
export class CreateReport {

  type = new FormControl<WaterReportKind | null>(null, Validators.required);
  typeFormOptions: { label: string, value: WaterReportKind }[] = [
    {label: 'Percentage', value: WaterReportKind.PERCENTAGE},
    {label: 'Swap', value: WaterReportKind.SWAP},
    {label: 'Refill', value: WaterReportKind.REFILL},
  ];

  selectedType = signal<WaterReportKind | null>(null);

  typeDescription = computed<string | null>(() => {
    switch (this.selectedType()) {
      case WaterReportKind.PERCENTAGE:
        return 'Report how much water is present in the current gallon.';
      case WaterReportKind.SWAP:
        return 'Report a swap: an empty ballon removed and replaced with a full one.';
      case WaterReportKind.REFILL:
        return 'All empty gallons have been refilled.';
      default:
        return null;
    }
  });

  confirmTitle = computed<string>(() => {
    switch (this.selectedType()) {
      case WaterReportKind.PERCENTAGE:
        return 'How Much Water Is in the Gallon?';
      default:
        return 'Confirm Your Report';
    }
  });

  messageService = inject(MessageService);
  service = inject(RevizitService);
  router = inject(Router);

  submitting = signal(false);
  waterLevel = signal(this.service.waterState().waterLevel);

  flavours = computed(() => [...this.service.waterFlavours().values()]);
  selectedFlavour = signal<number>(this.service.waterState().flavour.id);

  submitDisabled = computed(() => {
    const type = this.selectedType();
    const waterLevel = this.waterLevel();
    const selectedFlavour = this.selectedFlavour();
    return !type || !selectedFlavour || (type === WaterReportKind.PERCENTAGE && (waterLevel < 0 || waterLevel > 100) || type === WaterReportKind.SWAP && !this.service.waterFlavours().has(selectedFlavour));
  });

  constructor(private fb: FormBuilder) {
    this.type.valueChanges.subscribe(value => {
      this.selectedType.set(value);
    });
    this.service.loadWaterFlavours();
  }

  protected readonly PrimeIcons = PrimeIcons;

  submitReport() {
    this.submitting.set(true);
    this.service.submitWaterReport({
      value: this.waterLevel(),
      kind: this.selectedType()!,
      flavourId: this.selectedFlavour(),
    }).then(() => {
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Report submitted successfully!',
        life: 3000
      });
      this.submitting.set(false);
      this.router.navigateByUrl('/home');
    }).catch(err => {
      console.error('Error: ', err);
      this.submitting.set(false);
    })

  }

  protected readonly WaterReportKind = WaterReportKind;
}
