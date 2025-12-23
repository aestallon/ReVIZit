import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './component/header';
import {Footer} from './component/footer';
import {WaterBackground} from './component/water-background';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, WaterBackground],
  template: `
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
    }
  `,
})
export class App {
}
