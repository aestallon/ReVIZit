import { Component } from '@angular/core';
import { PrimeIcons } from 'primeng/api';

@Component({
  selector: 'app-footer',
  standalone: true,
  template: `
    <footer class="footer-container">
      <div class="footer-content">
        <div class="footer-left">
          <span class="app-name">ReVIZit</span>
          <span class="copyright">&copy; {{ currentYear }} All rights reserved.</span>
        </div>
        <div class="footer-right">
          <a href="https://github.com/aestallon/ReVIZit" target="_blank" rel="noopener noreferrer" class="github-link">
            <i [class]="PrimeIcons.GITHUB" class="github-icon"></i>
            <span>GitHub</span>
          </a>
        </div>
      </div>
    </footer>
  `,
  styles: `
    :host {
      display: block;
    }

    .footer-container {
      background: var(--p-content-background);
      border-top: 1px solid var(--p-content-border-color);
      padding: 1.5rem 2rem;
      z-index: 5angul;
    }

    .footer-content {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 2rem;
    }

    .footer-left {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .app-name {
      font-weight: 700;
      color: var(--p-primary-color);
      font-size: 1.1rem;
    }

    .copyright {
      color: var(--p-text-secondary-color);
      font-size: 0.875rem;
    }

    .github-link {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      text-decoration: none;
      color: var(--p-text-color);
      font-size: 0.875rem;
      transition: color 0.2s;
    }

    .github-link:hover {
      color: var(--p-primary-color);
    }

    .github-icon {
      font-size: 1.25rem;
    }

    @media (max-width: 640px) {
      .footer-content {
        flex-direction: column;
        gap: 1rem;
        text-align: center;
      }

      .footer-left {
        flex-direction: column;
        gap: 0.25rem;
      }
    }
  `
})
export class Footer {
  readonly PrimeIcons = PrimeIcons;
  readonly currentYear = new Date().getFullYear();
}
