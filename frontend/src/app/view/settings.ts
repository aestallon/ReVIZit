import {Component, inject} from '@angular/core';
import {Card} from 'primeng/card';
import {Panel} from 'primeng/panel';
import {RevizitService} from '../service/revizit.service';
import {FormsModule} from '@angular/forms';
import {WaterStateDef} from '../component/settings/water-state-def';
import {WaterFlavourDef} from '../component/settings/water-flavour-def';
import {UserDef} from '../component/settings/user-def';
import {WaterRollback} from '../component/settings/water-rollback';

@Component({
  selector: 'app-settings',
  template: `
    <p-card header="Settings" class="settings-card">
      <div class="settings-content">
        <p-panel header="Define the Current State">
          <app-water-state-def></app-water-state-def>
          <app-water-rollback></app-water-rollback>
        </p-panel>
        <p-panel header="Flavours">
          <app-water-flavour-def></app-water-flavour-def>
        </p-panel>
        <p-panel header="Users">
          <app-user-def></app-user-def>
        </p-panel>
      </div>
    </p-card>
  `,
  imports: [
    Card,
    Panel,
    FormsModule,
    WaterStateDef,
    WaterFlavourDef,
    UserDef,
    WaterRollback
  ],
  styles: `
    .settings-card {
      /*width: 700px;*/
    }

    :host ::ng-deep .p-panel {
      margin-bottom: 0.5rem;
    }
  `
})
export class Settings {

}
