import {Component} from '@angular/core';
import {Divider} from 'primeng/divider';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ProfileBasicDataForm} from './profile-basic-data-form';
import {ProfileBasicDataPic} from './profile-basic-data-pic';

@Component({
  selector: 'app-profile-basic-data',
  template: `
    <div class="basic-data-container">
      <app-profile-basic-data-pic></app-profile-basic-data-pic>
      <p-divider layout="vertical"></p-divider>
      <app-profile-basic-data-form class="basic-data-form"></app-profile-basic-data-form>
    </div>
  `,
  imports: [
    Divider,
    ReactiveFormsModule,
    FormsModule,
    ProfileBasicDataForm,
    ProfileBasicDataPic
  ],
  styles: `
  .basic-data-container {
    display: flex;
    flex-direction: row;
  }
  .basic-data-form {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  `
})
export class ProfileBasicData {

}
