import {Routes} from '@angular/router';
import {Home} from './view/home';
import {Login} from './view/login';
import {Settings} from './view/settings';
import {Statistics} from './view/statistics';
import {Reports} from './view/reports';
import {AUTH_GUARD} from './auth/auth';
import {CreateReport} from './view/create.report';

export const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: Home},
  {path: 'login', component: Login},
  {path: 'create-report', component: CreateReport, canActivate: [AUTH_GUARD]},
  {path: 'settings', component: Settings, canActivate: [AUTH_GUARD]},
  {path: 'stats', component: Statistics, canActivate: [AUTH_GUARD]},
  {path: 'reports', component: Reports, canActivate: [AUTH_GUARD]},

  {path: '**', redirectTo: '/home'}

];
