import {Routes} from '@angular/router';
import {Home} from './view/home';
import {Login} from './view/login';

export const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: Home},
  {path: 'login', component: Login},
  {path: '**', redirectTo: '/home'}

];
