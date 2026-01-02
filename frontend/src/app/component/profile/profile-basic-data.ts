import {Component, computed, inject} from '@angular/core';
import {Divider} from 'primeng/divider';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MessageService, PrimeIcons} from 'primeng/api';
import {DomSanitizer} from '@angular/platform-browser';
import {UserService} from '../../service/user.service';
import {ProfileService} from '../../../api/revizit';
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

  protected readonly PrimeIcons = PrimeIcons;

  userService = inject(UserService);
  profileService = inject(ProfileService);
  sanitizer = inject(DomSanitizer);
  messageService = inject(MessageService);

  profile = computed(() => this.userService.profile()!);


}
