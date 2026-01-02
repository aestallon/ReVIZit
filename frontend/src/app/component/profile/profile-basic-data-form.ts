import {Component, inject, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../service/user.service';
import {MessageService, PrimeIcons} from 'primeng/api';
import {asErrorMsg} from '../../service/errors';

@Component({
  selector: 'app-profile-basic-data-form',
  template: `
    <div class="basic-data-form-container">
      <p-float-label>
        <label for="on_displayName">Display Name</label>
        <input pInputText id="displayName" [(ngModel)]="editName"/>
      </p-float-label>
      <p-float-label>
        <label for="on_email">E-mail Address</label>
        <input pInputText id="email" [(ngModel)]="editEmail"/>
      </p-float-label>
      <p-button label="Save Changes" class="save-btn"
                [icon]="PrimeIcons.SAVE"
                (onClick)="onSaveClicked()">
      </p-button>
    </div>
  `,
  imports: [
    Button,
    FloatLabel,
    InputText,
    ReactiveFormsModule,
    FormsModule
  ],
  styles: `
    .basic-data-form-container {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }

    .save-btn {
        align-self: flex-end;
    }
  `
})
export class ProfileBasicDataForm {

  protected readonly PrimeIcons = PrimeIcons;

  userService = inject(UserService);
  messageService = inject(MessageService);
  editName = signal(this.userService.profile()!.data.name ?? '');

  editEmail = signal(this.userService.profile()!.data.email ?? '');

  onSaveClicked() {
    this.userService
      .updateBasicData(this.editName(), this.editEmail())
      .then(() => this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Profile updated successfully',
        life: 3000
      }))
      .catch(err => this.messageService.add(asErrorMsg(err, 'Failed to update profile data')));
  }
}
