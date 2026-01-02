import {Component, inject, signal} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {Header} from './component/header';
import {Footer} from './component/footer';
import {WaterBackground} from './component/water-background';
import {Toast} from 'primeng/toast';
import {ProgressSpinner} from 'primeng/progressspinner';
import {NavigationService} from './service/navigation.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, WaterBackground, Toast, ProgressSpinner],
  template: `
    <p-toast position="bottom-right"></p-toast>
    @if (nav.navigating()) {
      <p-progress-spinner class="progress-spinner"
                          strokeWidth="8"
                          ariaLabel="loading">
      </p-progress-spinner>
    }
    <app-water-background></app-water-background>
    <app-header></app-header>
    <main class="app-main">
      <router-outlet></router-outlet>
    </main>
    <app-footer></app-footer>
  `,
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .app-main {
      flex: 1;
      padding: 2rem;
      position: relative;
      z-index: 1;
      justify-content: center;
    }

    .progress-spinner {
      position: fixed;
      z-index: 999;
      height: 2em;
      width: 2em;
      margin: auto;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
    }

    /* Transparent Overlay */
    .progress-spinner:before {
      content: '';
      display: block;
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0,0,0,0.53);
    }
  `,
})
export class App {

  nav = inject(NavigationService);

}
