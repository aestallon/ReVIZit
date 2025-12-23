import {CanActivateFn, RedirectFunction, Router} from '@angular/router';
import {computed, effect, Inject, inject, signal} from '@angular/core';
import {HttpEventType, HttpInterceptorFn} from '@angular/common/http';
import {lastValueFrom, tap} from 'rxjs';
import {AuthService} from '../../api/revizit';

const TOKEN_KEY = 'revizit-token';

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

export const onLogout: (router: Router) => Promise<void> = async (router) => {
  localStorage.removeItem(TOKEN_KEY);
  await router.navigateByUrl('');
}

@Inject({providedIn: 'root'})
export class UserService {

  private readonly token = signal<string | null>(null);
  private readonly api = inject(AuthService);
  private readonly router = inject(Router);
  private readonly userEffect = effect(() => {
    console.log('User effect triggered');
    if (this.isAuthenticated()) {
      // TODO: fetch user data from API here
      // this.api.getUser().subscribe(user => this.user.set(user));
    } else {
      this.clearToken();
    }
  });

  public readonly isAuthenticated = computed(() => !!this.token());

  constructor() {
    this.token.set(localStorage.getItem(TOKEN_KEY));
  }

  public getToken(): string | null {
    return this.token();
  }

  public clearToken(): void {
    this.token.set(null);
    localStorage.removeItem(TOKEN_KEY);
  }

  private setToken(token: string): void {
    this.token.set(token);
    localStorage.setItem(TOKEN_KEY, token);
  }

  public async logOut(): Promise<void> {
    this.clearToken();
    await this.router.navigateByUrl('');
  }

  public async logIn(username: string, password: string): Promise<boolean> {
    try {
      const {token} = await lastValueFrom(this.api.login({username, password}));
      if (!token) {
        return false;
      }

      this.setToken(token);
      return true;

    } catch (e) {
      return false;
    }

  }


}
