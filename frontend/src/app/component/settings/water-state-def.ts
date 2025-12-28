import {Component, computed, inject, signal} from '@angular/core';
import {RevizitService} from '../../service/revizit.service';
import {Button} from 'primeng/button';
import {FloatLabel} from 'primeng/floatlabel';
import {InputNumber} from 'primeng/inputnumber';
import {Message} from 'primeng/message';
import {WaterGallonComponent} from '../gallon';
import {MessageService, PrimeIcons} from 'primeng/api';
import {FormsModule} from '@angular/forms';
import {Select} from 'primeng/select';
import {asCustomErrorMsg} from '../../service/errors';


@Component({
  selector: 'app-water-state-def',
  standalone: true,
  template: `
    <div class="state-container">
      <app-water-gallon [(waterLevel)]="waterLevel" [editable]="true"></app-water-gallon>
      <div class="state-form">
        <p-float-label>
          <p-input-number [(ngModel)]="waterLevel"
                          inputId="minmax"
                          id="minmax"
                          mode="decimal"
                          suffix="%"
                          autocomplete="off"
                          [invalid]="waterLevel() < 0 || waterLevel() > 100"
                          [min]="0"
                          [max]="100">
          </p-input-number>
          <label for="on_minmax">Current Water Level</label>
        </p-float-label>
        <p-float-label>
          <p-input-number [(ngModel)]="emptyCount"
                          id="empty"
                          inputId="empty"
                          mode="decimal"
                          autocomplete="off"
                          [invalid]="emptyCount() < 0 || emptyCount() > 100"
                          [min]="0"
                          [max]="100">
          </p-input-number>
          <label for="on_empty">Count of Empty Gallons</label>
        </p-float-label>
        <p-float-label>
          <p-input-number [(ngModel)]="fullCount"
                          inputId="full"
                          id="full"
                          mode="decimal"
                          autocomplete="off"
                          [invalid]="fullCount() < 0 || fullCount() > 100"
                          [min]="0"
                          [max]="100">
          </p-input-number>
          <label for="on_full">Count of Full Gallons</label>
        </p-float-label>
        <p-float-label>
          <p-select [options]="flavourOptions()"
                    [(ngModel)]="flavour"
                    id="flavour"
                    inputId="flavour"
                    optionLabel="name"
                    optionValue="id">
          </p-select>
          <label for="on_flavour">Current Flavour</label>
        </p-float-label>
      </div>
      <p-message severity="info" icon="pi pi-info-circle" class="settings-message">
        <p>You can define the current state of the water gallons here.</p>
        <p>This may be used to set the initial state of the application, or rectify an
          unreconciliable divergence between reports and reality.</p>
        <p>Defining the state here has no effect on pending reports.</p>
      </p-message>
      <span class="spacer"></span>
      <p-button label="Save"
                [loading]="loading()"
                [disabled]="unavailable()"
                [icon]="PrimeIcons.SAVE"
                severity="primary"
                class="settings-btn"
                (onClick)="onStateSaved()">
      </p-button>
    </div>
  `,
  imports: [
    Button,
    FloatLabel,
    InputNumber,
    Message,
    WaterGallonComponent,
    FormsModule,
    Select
  ],
  styles: `
    .state-container {
      display: flex;
      flex-direction: row;
      gap: 2rem;
      padding: 0.75em 1em;
    }

    .state-form {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }

    .settings-message {
      height: fit-content;
    }

    .settings-btn {
      align-self: flex-end;
    }

    .spacer {
      flex: 1;
    }
  `
})
export class WaterStateDef {

  service = inject(RevizitService);
  messageService = inject(MessageService);

  waterLevel = signal(this.service.waterState().waterLevel);
  emptyCount = signal(this.service.waterState().emptyGallons);
  fullCount = signal(this.service.waterState().fullGallons);

  flavourOptions = computed(() => [...this.service.waterFlavours().values()]);
  flavour = signal(this.service.waterState().flavour.id);

  loading = signal(true);
  unavailable = signal(false);

  protected readonly PrimeIcons = PrimeIcons;

  constructor() {
    this.service.loadWaterState()
      .then(() => {
        this.waterLevel.set(this.service.waterState().waterLevel);
        this.emptyCount.set(this.service.waterState().emptyGallons);
        this.fullCount.set(this.service.waterState().fullGallons);
        this.flavour.set(this.service.waterState().flavour.id);
        this.loading.set(false);
      })
      .catch((err) => {
        this.loading.set(false);
        this.unavailable.set(true);
        this.messageService.add(asCustomErrorMsg(err, 'Failed to load water state data'));
      });
  }

  onStateSaved() {
    this.loading.set(true);
    this.service
      .defineState({
        waterLevel: this.waterLevel(),
        fullGallons: this.fullCount(),
        emptyGallons: this.emptyCount(),
        flavour: {
          id: this.flavour(),
          name: '',
          inactive: false,
        },
        reportedAt: new Date().toISOString(),
      })
      .then(() => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'State saved successfully!',
          life: 3000
        });
        this.loading.set(false);
      })
      .catch(err => {
        this.messageService.add(asCustomErrorMsg(err, 'Failed to save water state'));
        this.loading.set(false);
      });
  }
}
