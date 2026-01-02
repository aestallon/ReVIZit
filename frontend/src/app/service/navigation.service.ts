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

  constructor() {
    this.router.events.subscribe((e) => {
      if (e instanceof NavigationStart) {
        this.navigating.set(true);
      }

      if (e instanceof NavigationEnd || e instanceof NavigationCancel || e instanceof NavigationError) {
        this.navigating.set(false);
      }
    });
  }

}
