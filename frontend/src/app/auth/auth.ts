import {CanActivateFn, RedirectFunction, Router} from '@angular/router';
import {inject} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpEventType, HttpInterceptorFn} from '@angular/common/http';
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
  const userService = inject(UserService);
  const router = inject(Router);
  return next(req).pipe(tap({
    next: (event: HttpEvent<unknown>) => {
      if (event.type !== HttpEventType.Response) {
        return;
      }

      if (!(event.status === 401 || event.status === 403)) {
        return;
      }

      userService.clearToken();
      router.navigateByUrl('login');
    },
    error: (error: HttpErrorResponse): void => {
      if (!(error.status === 401 || error.status === 403)) {
        return;
      }

      userService.clearToken();
      router.navigateByUrl('login');
    }
  }));
}
