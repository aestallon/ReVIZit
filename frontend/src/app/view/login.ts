import {Component, inject, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {InputText} from 'primeng/inputtext';
import {Password} from 'primeng/password';
import {Card} from 'primeng/card';
import {FormsModule} from '@angular/forms';
import {Message} from 'primeng/message';
import {Router} from '@angular/router';
import {UserService} from '../service/user.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    Card,
    FormsModule,
    InputText,
    Password,
    Message,
    Button
  ],
  template: `
    <div class="login-container">
      <p-card header="Login" [style]="{ width: '400px' }">
        <form (ngSubmit)="onLogin()" #loginForm="ngForm" class="login-form">
          <div class="field">
            <label for="username">Username</label>
            <input
              id="username"
              type="text"
              pInputText
              [(ngModel)]="username"
              name="username"
              required
              autofocus
              class="w-full"
            />
          </div>

          <div class="field">
            <label for="password">Password</label>
            <p-password
              id="password"
              [(ngModel)]="password"
              name="password"
              [feedback]="false"
              [toggleMask]="true"
              class="w-full"
              inputStyleClass="w-full"
              [required]="true"
            />
          </div>

          @if (error()) {
            <p-message severity="error" class="w-full mb-3">
              {{ error() }}
            </p-message>
          }

          <div class="actions">
            <p-button
              label="Login"
              type="submit"
              [loading]="loading()"
              [disabled]="!loginForm.form.valid || loading()"
              class="w-full"
              styleClass="w-full"
            />
          </div>
        </form>
      </p-card>
    </div>
  `,
  styles: `
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      padding: 2rem;
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .field {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .field label {
      font-weight: 600;
      font-size: 0.875rem;
    }

    .w-full {
      width: 100%;
    }

    :host ::ng-deep {
      .p-password input {
        width: 100%;
      }
      .p-card-title {
        text-align: center;
        margin-bottom: 1rem;
      }
    }

    .actions {
      margin-top: 1rem;
    }
  `
})
export class Login {
  private readonly service = inject(UserService);
  private readonly router = inject(Router);

  username = '';
  password = '';
  loading = signal(false);
  error = signal<string | undefined>(undefined);

  async onLogin() {
    if (!this.username || !this.password) return;

    this.loading.set(true);
    this.error.set(undefined);

    try {
      const success = await this.service.logIn(this.username, this.password);
      if (success) {
        await this.router.navigateByUrl('/');
      } else {
        this.error.set('Invalid username or password');
      }
    } catch (e) {
      this.error.set('An error occurred during login');
    } finally {
      this.loading.set(false);
    }
  }
}
