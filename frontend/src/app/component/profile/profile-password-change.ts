import {Component, inject, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {Password} from 'primeng/password';
import {FormsModule} from '@angular/forms';
import {UserService} from '../../service/user.service';
import {MessageService, PrimeIcons} from 'primeng/api';
import {asErrorMsg} from '../../service/errors';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-profile-password-change',
  template: `
    <div class="pw-change-container">
      <div class="pw-change-form">
        <p-float-label>
          <label for="on_oldPassword">Old Password</label>
          <p-password id="oldPassword"
                      [(ngModel)]="oldPassword"
                      [feedback]="false"
                      placeholder="Enter current password..."
                      [invalid]="oldPassword().length === 0"
                      [toggleMask]="true">
          </p-password>
        </p-float-label>
        <p-float-label>
          <label for="on_newPassword">New Password</label>
          <p-password id="newPassword" [(ngModel)]="newPassword"
                      placeholder="Enter new password..."
                      [toggleMask]="true"
                      [invalid]="newPassword().length === 0">
          </p-password>
        </p-float-label>
        <p-float-label>
          <label for="on_newPassword2">New Password</label>
          <p-password id="newPassword2" [(ngModel)]="newPassword2"
                      placeholder="Repeat new password..."
                      [invalid]="newPassword2() !== newPassword()"
                      [toggleMask]="true">
          </p-password>
        </p-float-label>
        @if (errorMsg().length > 0) {
          <p-message [icon]="PrimeIcons.EXCLAMATION_CIRCLE"
                     severity="error">
            <span>{{ errorMsg() }}</span>
          </p-message>
        }
        <p-button class="pw-save-btn"
                  label="Change Password"
                  [icon]="PrimeIcons.LOCK"
                  (onClick)="changePassword()">
        </p-button>
      </div>

    </div>
  `,
  imports: [
    Button,
    Password,
    FormsModule,
    FloatLabel,
    Message
  ],
  styles: `
    .pw-change-container {
      display: flex;
      flex-direction: row;
    }
  .pw-change-form {
    display: flex;
    flex-direction: column;
    gap: 2rem;
    padding-top: 2em;
    padding-bottom: 2em;
  }

  .pw-save-btn {
    align-self: flex-end;
  }
  `
})
export class ProfilePasswordChange {

  oldPassword = signal('');
  newPassword = signal('');
  newPassword2 = signal('');

  userService = inject(UserService);
  messageService = inject(MessageService);

  errorMsg = signal('');
  loading = signal(false);

  changePassword() {
    this.loading.set(true);
    if (this.oldPassword().length < 1) {
      this.errorMsg.set('Please enter your current password');
      this.loading.set(false);
      return;
    }

    if (this.newPassword().length < 8) {
      this.errorMsg.set('New password must be at least 8 characters long');
      this.loading.set(false);
      return;
    }

    if (this.newPassword2() !== this.newPassword()) {
      this.errorMsg.set('New passwords do not match');
      this.loading.set(false);
      return;
    }

    this.userService.changePassword(this.oldPassword(), this.newPassword())
      .then(() => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Password changed'
        });
        this.oldPassword.set('');
        this.newPassword.set('');
        this.newPassword2.set('');
        this.errorMsg.set('');
      })
      .catch(err => {
        this.loading.set(false);
        const errMsg = asErrorMsg(err, 'Failed to change password');
        this.errorMsg.set(errMsg.detail!);
        this.messageService.add(errMsg);
      });
  }

  protected readonly PrimeIcons = PrimeIcons;
}
