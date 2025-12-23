import {Routes} from '@angular/router';
import {Home} from './view/home';
import {Login} from './view/login';
import {Settings} from './view/settings';
import {Statistics} from './view/statistics';
import {Reports} from './view/reports';

export const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: Home},
  {path: 'login', component: Login},
  {path: 'settings', component: Settings},
  {path: 'stats', component: Statistics},
  {path: 'reports', component: Reports},

  {path: '**', redirectTo: '/home'}

];
