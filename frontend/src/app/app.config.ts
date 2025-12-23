import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection
} from '@angular/core';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withInMemoryScrolling, withViewTransitions
} from '@angular/router';
import Aura from '@primeuix/themes/aura';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {INTERCEPTOR_REQUEST, INTERCEPTOR_RESPONSE} from './auth/auth';
import {providePrimeNG} from 'primeng/config';
import {definePreset} from '@primeuix/themes';
import {MessageService} from 'primeng/api';
import {BASE_PATH} from '../api/revizit';
import {UserService} from './service/user.service';

const Theme = definePreset(Aura, {});

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes,
      withInMemoryScrolling({
        anchorScrolling: 'enabled',
        scrollPositionRestoration: 'enabled',
      }),
      withEnabledBlockingInitialNavigation(),
      withViewTransitions()),
    provideHttpClient(withInterceptors([INTERCEPTOR_REQUEST, INTERCEPTOR_RESPONSE])),
    providePrimeNG({
      theme: {
        preset: Theme,
        options: {darkModeSelector: '.revizit-dark'}
      }
    }),
    MessageService,
    {provide: BASE_PATH, useValue: '/api'},
    UserService,
  ]
};
