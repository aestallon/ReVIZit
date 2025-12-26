import {Component, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {Panel} from 'primeng/panel';
import {UserService} from '../service/user.service';
import {RevizitService} from '../service/revizit.service';
import {WaterGallonComponent} from '../component/gallon';
import {InputNumber} from 'primeng/inputnumber';
import {FormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';
import {Button} from 'primeng/button';
import {PrimeIcons} from 'primeng/api';

@Component({
  selector: 'app-settings',
  template: `
    <p-card header="Settings" class="settings-card">
      <div class="settings-content">
        <p-panel header="Define the Current State">
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
                                mode="decimal"
                                autocomplete="off"
                                [invalid]="fullCount() < 0 || fullCount() > 100"
                                [min]="0"
                                [max]="100">
                </p-input-number>
                <label for="on_full">Count of Full Gallons</label>
              </p-float-label>
            </div>
            <p-message severity="secondary" icon="pi pi-info-circle" class="settings-message">
              <p>You can define the current state of the water gallons here.</p>
              <p>This may be used to set the initial state of the application, or rectify an
                unreconciliable divergence between reports and reality.</p>
              <p>Defining the state here has no effect on pending reports.</p>
            </p-message>
            <span class="spacer"></span>
            <p-button label="Save"
                      [icon]="PrimeIcons.SAVE"
                      severity="primary"
                      class="settings-btn"
                      (onClick)="onStateSaved()">
            </p-button>
          </div>
        </p-panel>
        <p-panel header="Flavours">

        </p-panel>
        <p-panel header="Users">

        </p-panel>
      </div>
    </p-card>
  `,
  imports: [
    Card,
    Panel,
    WaterGallonComponent,
    InputNumber,
    FormsModule,
    FloatLabel,
    Message,
    Button
  ],
  styles: `
    .settings-card {
      /*width: 700px;*/
    }
    .state-container {
      display: flex;
      flex-direction: row;
      gap: 2rem;
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
export class Settings {

  userService = inject(UserService);
  service = inject(RevizitService);


  waterLevel = signal(this.service.waterState().waterLevel);
  emptyCount = signal(this.service.waterState().emptyGallons);
  fullCount = signal(this.service.waterState().fullGallons);

  protected readonly PrimeIcons = PrimeIcons;

  onStateSaved() {

  }
}
