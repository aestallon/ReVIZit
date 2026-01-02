import {Component} from '@angular/core';
import {Divider} from 'primeng/divider';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ProfileBasicDataForm} from './profile-basic-data-form';
import {ProfileBasicDataPic} from './profile-basic-data-pic';
import {Message} from 'primeng/message';
import {PrimeIcons} from 'primeng/api';

@Component({
  selector: 'app-profile-basic-data',
  template: `
    <div class="basic-data-container">
      <app-profile-basic-data-pic></app-profile-basic-data-pic>
      <p-divider layout="vertical"></p-divider>
      <app-profile-basic-data-form class="basic-data-form"></app-profile-basic-data-form>
      <p-divider layout="vertical"></p-divider>
      <p-message class="info-block" severity="info" [icon]="PrimeIcons.INFO_CIRCLE">
        <p>You may select an image an image not larger than <strong>5 MB</strong> and crop it to set
          it as your profile picture, by clicking the <i [class]="PrimeIcons.UPLOAD"></i><strong>
            Choose</strong> button. <em>This change takes effect immediately after completion.</em>
        </p>
        <p>You can also set your <strong>display name</strong>: this is the name by which you appear
          throughout the application for yourself and others.</p>
        <p>The application may send mails to your <strong>e-mail address</strong> to notify you
          about events of interest, or aid you in account recovery.</p>
        <p><em>You can confirm the changes you've made to your <strong>display name</strong> and
          <strong>e-mail address</strong> by clicking <i [class]="PrimeIcons.SAVE"></i><strong> Save
            Changes</strong></em></p>
      </p-message>
    </div>
  `,
  imports: [
    Divider,
    ReactiveFormsModule,
    FormsModule,
    ProfileBasicDataForm,
    ProfileBasicDataPic,
    Message
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

  .info-block {
    height: fit-content;
  }
  `
})
export class ProfileBasicData {

  protected readonly PrimeIcons = PrimeIcons;
}
