import {Routes} from '@angular/router';
import {Home} from './view/home';
import {AUTH_GUARD} from './auth/auth';

export const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: Home},
  {path: 'login', loadComponent: () => import('./view/login').then(m => m.Login)},
  {
    path: 'create-report',
    loadComponent: () => import('./view/create.report').then(m => m.CreateReport),
  },
  {
    path: 'settings',
    loadComponent: () => import('./view/settings').then(m => m.Settings),
    canActivate: [AUTH_GUARD]
  },
  {
    path: 'stats',
    loadComponent: () => import('./view/statistics').then(m => m.Statistics),
    canActivate: [AUTH_GUARD]
  },
  {
    path: 'reports',
    loadComponent: () => import('./view/reports').then(m => m.Reports),
    canActivate: [AUTH_GUARD]
  },
  {
    path: 'profile',
    loadComponent: () => import('./view/profile').then(m => m.Profile),
    canActivate: [AUTH_GUARD]
  },
  {
    path: 'logs',
    loadComponent: () => import('./view/system-logs').then(m => m.SystemLogs),
    canActivate: [AUTH_GUARD],
  },

  {path: '**', redirectTo: '/home'}

];
