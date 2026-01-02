import {Component, inject, signal} from '@angular/core';
import {Card} from 'primeng/card';
import {Panel} from 'primeng/panel';
import {Button} from 'primeng/button';
import {FormsModule} from '@angular/forms';
import {MessageService, PrimeIcons} from 'primeng/api';
import {ProfileBasicData} from '../component/profile/profile-basic-data';
import {UserService} from '../service/user.service';
import {ProfilePasswordChange} from '../component/profile/profile-password-change';
import {asErrorMsg} from '../service/errors';

@Component({
  selector: 'app-profile',
  template: `
    <p-card header="Profile">
      <div class="profile-content">
        <p-panel header="Basic Data">
          <app-profile-basic-data></app-profile-basic-data>
        </p-panel>

        <p-panel header="Change Password">
          <app-profile-password-change></app-profile-password-change>
        </p-panel>

        <p-panel header="Delete Account">
          <div class="delete-section">
            <p><strong>Once you delete your account, there is no going back.</strong></p>
            <p-button label="Delete Account"
                      [icon]="PrimeIcons.TRASH"
                      severity="danger"
                      (onClick)="deleteAccount()"
                      [loading]="underDelete()"></p-button>
          </div>
        </p-panel>
      </div>
    </p-card>
  `,
  styles: `
    .profile-content {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .delete-section {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    :host ::ng-deep .p-panel {
      margin-bottom: 0.5rem;
    }
  `,
  imports: [
    Card,
    Panel,
    Button,
    FormsModule,
    ProfileBasicData,
    ProfilePasswordChange
  ]
})
export class Profile {

  userService = inject(UserService);
  msg = inject(MessageService);
  underDelete = signal(false);


  deleteAccount() {
    if (!confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
      return;
    }

    this.underDelete.set(true);
    this.userService.deleteMe()
      .then(() => this.underDelete.set(false))
      .catch((err) => {
        this.underDelete.set(false);
        this.msg.add(asErrorMsg(err, 'Failed to delete account'));
      });
  }

  protected readonly PrimeIcons = PrimeIcons;
}
