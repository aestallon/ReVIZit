import {Injectable, signal, WritableSignal} from '@angular/core';


const DARK_MODE_KEY = 'darkMode';

@Injectable({
  providedIn: 'root'
})
export class DarkmodeService {

  public readonly isDark: WritableSignal<boolean>;

  constructor() {
    const savedMode = localStorage.getItem(DARK_MODE_KEY) === 'true';
    this.isDark = signal(savedMode);
    if (savedMode) {
      document.documentElement.classList.add('revizit-dark');
    } else {
      document.documentElement.classList.remove('revizit-dark');
    }
  }

  public toggleDarkMode() {
    this.isDark.set(!this.isDark());
    document.documentElement.classList.toggle('revizit-dark');
    localStorage.setItem(DARK_MODE_KEY, this.isDark().toString());
  }

}
