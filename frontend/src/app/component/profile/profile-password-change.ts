import {Component, inject, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {Password} from 'primeng/password';
import {FormsModule} from '@angular/forms';
import {UserService} from '../../service/user.service';
import {MessageService, PrimeIcons} from 'primeng/api';
import {asCustomErrorMsg} from '../../service/errors';
import {FloatLabel} from 'primeng/floatlabel';

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
                      [toggleMask]="true">
          </p-password>
        </p-float-label>
        <p-float-label>
          <label for="on_newPassword">New Password</label>
          <p-password id="newPassword" [(ngModel)]="newPassword"
                      placeholder="Enter new password..."
                      [toggleMask]="true">
          </p-password>
        </p-float-label>
        <p-float-label>
          <label for="on_newPassword2">New Password</label>
          <p-password id="newPassword2" [(ngModel)]="newPassword"
                      placeholder="Repeat new password..."
                      [toggleMask]="true">
          </p-password>
        </p-float-label>
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
    FloatLabel
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

  changePassword() {
    this.userService.changePassword(this.oldPassword(), this.newPassword())
      .then(() => this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Password changed'
      }))
      .catch(err => this.messageService.add(asCustomErrorMsg(err, 'Failed to change password')));
  }

  protected readonly PrimeIcons = PrimeIcons;
}
