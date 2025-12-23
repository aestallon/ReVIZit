import {Component, inject, signal} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {Button} from 'primeng/button';
import {UserService} from './auth/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Button, RouterLink],
  template: `
    <header class="app-header">
      <p-button routerLink="/home" label="Home" severity="secondary"></p-button>
      @if (userService.isAuthenticated()) {
        <p-button label="Log Out" severity="warn" (onClick)="onLogoutClicked($event)"></p-button>
      } @else {
        <p-button label="Log In" severity="primary" routerLink="/login"></p-button>
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

}
