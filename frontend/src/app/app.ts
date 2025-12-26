import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './component/header';
import {Footer} from './component/footer';
import {WaterBackground} from './component/water-background';
import {Toast} from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, WaterBackground, Toast],
  template: `
    <p-toast position="bottom-right"></p-toast>
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
  `,
})
export class App {
}
