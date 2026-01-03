import {computed, effect, inject, Inject, signal} from '@angular/core';
import {AuthService, Profile, ProfileService} from '../../api/revizit';
import {Router} from '@angular/router';
import {lastValueFrom, Observable, Subject, Subscription} from 'rxjs';
import {SseClient} from 'ngx-sse-client';
import {HttpHeaders} from '@angular/common/http';

const TOKEN_KEY = 'revizit-token';

@Inject({providedIn: 'root'})
export class UserService {

  private readonly token = signal<string | null>(null);
  private readonly authApi = inject(AuthService);
  private readonly profileApi = inject(ProfileService);
  private readonly router = inject(Router);
  private readonly sse = inject(SseClient);
  private readonly user = signal<Profile | null>(null);
  private readonly userEffect = effect(async () => {
    await this.refreshProfile();
    this.handleSseSubscription();
  });

  public readonly isAuthenticated = computed(() => !!this.token());
  public readonly profile = computed(() => this.user());

  private subscription: Subscription | null = null;
  _needStateRefresh = new Subject<void>();
  _needReportRefresh = new Subject<void>();

  constructor() {
    this.token.set(localStorage.getItem(TOKEN_KEY));
  }

  public getToken(): string | null {
    return this.token();
  }

  public clearToken(): void {
    this.token.set(null);
    this.user.set(null);
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

  async refreshProfile() {
    if (this.isAuthenticated()) {

      let profile: Profile | null = null;
      try {
        profile = await lastValueFrom(this.profileApi.getMyProfile());
      } catch (e) {
        console.error('Error on fetching profile: ', e);
      }
      this.user.set(profile);
    } else {
      this.clearToken();
    }
  }

  handleSseSubscription() {
    if (this.isAuthenticated()) {
      if (this.subscription) {
        this.subscription.unsubscribe();
        this.subscription = null;
      }

      const headers = new HttpHeaders().set('Authorization', `Bearer ${this.getToken()!}`);
      this.subscription = this.sse.stream(
        '/api/sse/water',
        {
          keepAlive: true,
          reconnectionDelay: 1_000,
          responseType: 'event'
        },
        {headers},
        'GET')
        .subscribe(event => {
          if (event.type === 'error') {
            const errorEvent = event as ErrorEvent;
            console.error(errorEvent.error, errorEvent.message);
            this.subscription?.unsubscribe();
          } else {
            const msg = event as MessageEvent;
            if ('WATER_STATE_CHANGED' === msg.data) {
              this._needStateRefresh.next();
            } else if ('PENDING_REPORTS_CHANGED' === msg.data) {
              this._needReportRefresh.next();
            } else {
              console.warn('Unknown SSE message type: ', msg.data);
            }
          }
        });
    } else if (this.subscription) {
      this.subscription.unsubscribe();
      this.subscription = null;
    }
  }

  public async logIn(username: string, password: string): Promise<boolean> {
    try {
      const {token} = await lastValueFrom(this.authApi.login({username, password}));
      if (!token) {
        return false;
      }

      this.setToken(token);
      return true;

    } catch (e) {
      return false;
    }

  }

  async updateBasicData(name: string, email: string) {
    const res = await lastValueFrom(this.profileApi.updateMyProfile({name, email}));
    this.user.set(res);
  }

  async updatePfp(blob: Blob) {
    const res = await lastValueFrom(this.profileApi.updateProfilePic(blob));
    this.user.update(it => {
      if (!it) return it;
      return {
        ...it,
        pfp: res.url
      }
    });
  }

  async changePassword(from: string, to: string) {
    await lastValueFrom(this.profileApi.changeMyPassword({
      oldPassword: from,
      newPassword: to,
    }));
  }

  async deleteMe() {
    await lastValueFrom(this.profileApi.deleteMyProfile());
    await this.logOut();
  }

}
