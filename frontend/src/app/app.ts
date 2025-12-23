import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './component/header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header],
  template: `
    <app-header></app-header>
    <main class="app-main">
      <router-outlet></router-outlet>
    </main>
    <footer class="app-footer">

    </footer>
  `,
  styles: `
    .app-main {
      padding: 2rem;
    }

    .app-footer {
    }
  `,
})
export class App {
}
