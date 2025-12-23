import {computed, effect, inject, Inject, signal} from '@angular/core';
import {AuthService, Profile, ProfileService} from '../../api/revizit';
import {Router} from '@angular/router';
import {lastValueFrom} from 'rxjs';

const TOKEN_KEY = 'revizit-token';

@Inject({providedIn: 'root'})
export class UserService {

  private readonly token = signal<string | null>(null);
  private readonly authApi = inject(AuthService);
  private readonly profileApi = inject(ProfileService);
  private readonly router = inject(Router);
  private readonly user = signal<Profile | null>(null);
  private readonly userEffect = effect(async () => {
    console.log('User effect triggered');
    if (this.isAuthenticated()) {

      let profile: Profile | null = null;
      try {
        profile = await lastValueFrom(this.profileApi.getMyProfile());
      } catch (e) {
        console.error('Error on fetching profile: ', e);
      }
      this.user.set(profile);
      console.log('User profile: ', this.user());
    } else {
      this.clearToken();
    }
  });

  public readonly isAuthenticated = computed(() => !!this.token());
  public readonly profile = computed(() => this.user());

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

}
