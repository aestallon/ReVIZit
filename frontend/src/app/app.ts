import {Component, inject, signal} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {Button} from 'primeng/button';
import {UserService} from './service/user.service';
import {PrimeIcons} from 'primeng/api';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Button, RouterLink],
  template: `
    <header class="app-header">
      <h1>ReVIZit</h1>
      @if (userService.profile()) {
        <h3>Hello {{ userService.profile()?.name ?? 'there' }}!</h3>
      }
      <span class="spacer"></span>
      @if (userService.profile()?.isAdmin) {
        <p-button [icon]="PrimeIcons.COG" label="Settings"></p-button>
      }
      @if (userService.isAuthenticated()) {
        <p-button [icon]="PrimeIcons.LIST_CHECK" label="Pending Reports"></p-button>
      }
      <p-button routerLink="/home"
                [icon]="PrimeIcons.HOME" label="Home"
                severity="secondary"></p-button>
      @if (userService.isAuthenticated()) {
        <p-button label="Log Out"
                  [icon]="PrimeIcons.SIGN_OUT"
                  severity="warn"
                  (onClick)="onLogoutClicked($event)"></p-button>
      } @else {
        <p-button label="Log In"
                  [icon]="PrimeIcons.SIGN_IN"
                  severity="primary"
                  routerLink="/login"></p-button>
      }
    </header>
    <main class="app-main">
      <router-outlet></router-outlet>
    </main>
    <footer class="app-footer">

    </footer>
  `,
  styles: `
    .app-header {
      display: flex;
      justify-content: space-between;
      align-items: baseline;
    }

    .spacer {
      display: flex;
      flex: 1;
    }

    .app-main {
    }

    .app-footer {
    }
  `,
})
export class App {
  protected readonly userService = inject(UserService);

  onLogoutClicked(event: MouseEvent) {
   this.userService.logOut();
  }

  protected readonly PrimeIcons = PrimeIcons;
}
