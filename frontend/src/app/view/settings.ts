import {Component, inject} from '@angular/core';
import {Card} from 'primeng/card';
import {Panel} from 'primeng/panel';
import {UserService} from '../service/user.service';
import {RevizitService} from '../service/revizit.service';
import {FormsModule} from '@angular/forms';
import {PrimeIcons} from 'primeng/api';
import {WaterStateDef} from '../component/settings/water-state-def';
import {WaterFlavourDef} from '../component/settings/water-flavour-def';

@Component({
  selector: 'app-settings',
  template: `
    <p-card header="Settings" class="settings-card">
      <div class="settings-content">
        <p-panel header="Define the Current State" >
          <app-water-state-def></app-water-state-def>
        </p-panel>
        <p-panel header="Flavours">
          <app-water-flavour-def></app-water-flavour-def>
        </p-panel>
        <p-panel header="Users">

        </p-panel>
      </div>
    </p-card>
  `,
  imports: [
    Card,
    Panel,
    FormsModule,
    WaterStateDef,
    WaterFlavourDef
  ],
  styles: `
    .settings-card {
      /*width: 700px;*/
    }

  `
})
export class Settings {

  userService = inject(UserService);
  service = inject(RevizitService);


  protected readonly PrimeIcons = PrimeIcons;


}
