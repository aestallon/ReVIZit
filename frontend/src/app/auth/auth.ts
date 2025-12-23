import {CanActivateFn, RedirectFunction, Router} from '@angular/router';
import {inject} from '@angular/core';
import {HttpEventType, HttpInterceptorFn} from '@angular/common/http';
import {tap} from 'rxjs';
import {UserService} from '../service/user.service';


// Use something like this for redirection if necessary:
export const AUTH_REDIRECT: RedirectFunction = it => {
  return inject(UserService).isAuthenticated() ? 'a' : 'b';
};

export const AUTH_GUARD: CanActivateFn = (route, state) => {
  return inject(UserService).isAuthenticated() || inject(Router).parseUrl('/login');
}

export const INTERCEPTOR_REQUEST: HttpInterceptorFn = (req, next) => {
  const token = inject(UserService).getToken();
  if (!token || token.length < 1) {
    return next(req);
  }

  return next(req.clone({
    setHeaders: {Authorization: `Bearer ${token}`}
  }));
}

export const INTERCEPTOR_RESPONSE: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(tap(event => {
    if (event.type === HttpEventType.Response) {

      if (event.status === 401 || event.status === 403) {
        inject(UserService).clearToken();
        inject(Router).navigateByUrl('login');
      }

    }
  }));
}
