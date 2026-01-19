import {inject, Injectable, signal} from '@angular/core';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router
} from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  navigating = signal(false);
  router = inject(Router);
  timeoutId: number | null = null;

  constructor() {
    this.router.events.subscribe((e) => {
      if (e instanceof NavigationStart) {
        this.timeoutId = setTimeout(() => this.navigating.set(true), 300);
      }

      if (e instanceof NavigationEnd || e instanceof NavigationCancel || e instanceof NavigationError) {
        if (this.timeoutId !== null) {
          clearTimeout(this.timeoutId);
          this.timeoutId = null;
        }
        this.navigating.set(false);
      }
    });
  }

}
