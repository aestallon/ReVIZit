import {Component, inject} from '@angular/core';
import {AuthService} from '../../api/revizit';
import {UserService} from '../auth/auth';

@Component({
  selector: 'app-login',
  template: `
    <h1>Login</h1>
  `,
  styles: `
  `
})
export class Login {

  service = inject(UserService);

}
