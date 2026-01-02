import {Component, computed, inject} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {Button} from 'primeng/button';
import {UserService} from '../service/user.service';
import {MenuItem, PrimeIcons} from 'primeng/api';
import {RevizitService} from '../service/revizit.service';
import {Menu} from 'primeng/menu';
import {UserBtn} from './user.btn';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [Button, RouterLink, RouterLinkActive, Menu, UserBtn],
  template: `
    <header class="header-container">
      <div class="header-left">
        <a routerLink="/" class="logo">
          <span class="logo-text">ReVIZit</span>
        </a>
        @if (userService.profile()) {
          <div class="vertical-divider"></div>
          <div class="user-greeting">
            <i [class]="PrimeIcons.USER" class="user-icon"></i>
            <span>Hello, <span
              class="user-name">{{ userService.profile()?.data?.name ?? 'there' }}</span>!</span>
          </div>
        }
      </div>

      <div class="header-center">
      </div>

      <div class="header-right">
        <div class="nav-actions">
          @if (userService.isAuthenticated()) {
            <p-button [icon]="PrimeIcons.CHART_BAR"
                      routerLink="/stats"
                      routerLinkActive="active-route"
                      label="Statistics"
                      variant="text"></p-button>
            <p-button [icon]="PrimeIcons.LIST_CHECK"
                      routerLink="/reports"
                      routerLinkActive="active-route"
                      label="Pending Reports"
                      variant="text"
                      [badge]="pendingCount()"></p-button>
          }
          <p-button routerLink="/home"
                    routerLinkActive="active-route"
                    [icon]="PrimeIcons.HOME"
                    label="Home"
                    variant="text"></p-button>

          <div class="auth-section">
            @if (userService.isAuthenticated()) {
              <p-menu #menu [model]="userActions()" [popup]="true"></p-menu>
              <app-user-btn [profile]="userService.profile()?? undefined" (onClick)="menu.toggle($event)"></app-user-btn>
            } @else {
              <p-button label="Log In"
                        [icon]="PrimeIcons.SIGN_IN"
                        severity="primary"
                        routerLink="/login"></p-button>
            }
          </div>
        </div>
      </div>
    </header>
  `,
  styles: `
    :host {
      display: block;
    }

    .header-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 2rem;
      background: var(--p-content-background);
      border-bottom: 1px solid var(--p-content-border-color);
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      position: sticky;
      top: 0;
      z-index: 1000;
      min-height: 4rem;
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .header-left .logo {
      text-decoration: none;
      color: inherit;
      display: flex;
      align-items: center;
    }

    .vertical-divider {
      width: 1px;
      height: 1.5rem;
      background-color: var(--p-content-border-color);
    }

    .logo-text {
      font-size: 1.5rem;
      font-weight: 800;
      letter-spacing: -0.025em;
      background: linear-gradient(90deg, var(--p-primary-color), var(--p-primary-600));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .active-route {
      background-color: var(--p-primary-100);
      border-radius: 6px;
    }

    .header-center {
      flex: 1;
      display: flex;
      justify-content: center;
    }

    .user-greeting {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.9rem;
      color: var(--p-text-secondary-color);
    }

    .user-icon {
      font-size: 1rem;
      color: var(--p-primary-color);
    }

    .user-name {
      font-weight: 800;
      color: var(--p-text-color);
    }

    .header-right {
      display: flex;
      align-items: center;
    }

    .nav-actions {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .auth-section {
      margin-left: 0.5rem;
      padding-left: 0.5rem;
      border-left: 1px solid var(--p-content-border-color);
    }

    @media (max-width: 768px) {
      .header-container {
        padding: 0.75rem 1rem;
        flex-direction: column;
        gap: 1rem;
        height: auto;
      }

      .header-left {
        width: 100%;
        justify-content: center;
      }

      .header-center {
        order: 3;
        width: 100%;
      }

      .nav-actions {
        flex-wrap: wrap;
        justify-content: center;
      }
    }
  `
})
export class Header {
  protected readonly userService = inject(UserService);
  protected readonly revizitService = inject(RevizitService);
  protected readonly PrimeIcons = PrimeIcons;

  pendingCount = computed(() => {
    const cnt = this.revizitService.pendingReports().length;
    return cnt > 0 ? `${cnt}` : '';
  });

  userActions = computed<Array<MenuItem>>(() => {
    const profile = this.userService.profile();
    if (!profile) {
      return [];
    }

    const admin = profile.isAdmin;
    return admin
      ? [
        {label: 'Profile', icon: PrimeIcons.USER_EDIT, routerLink: '/profile'},
        {label: 'Settings', icon: PrimeIcons.COG, routerLink: '/settings'},
        {separator: true},
        {label: 'Log Out', icon: PrimeIcons.SIGN_OUT, command: () => this.onLogoutClicked(),},
      ]
      : [
        {label: 'Profile', icon: PrimeIcons.USER_EDIT, routerLink: '/profile'},
        {separator: true},
        {label: 'Log Out', icon: PrimeIcons.SIGN_OUT, command: () => this.onLogoutClicked()},
      ]
  });

  onLogoutClicked() {
    this.userService.logOut();
  }
}
