import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {WaterGallonComponent} from '../component/gallon';
import {DatePipe} from '@angular/common';
import {Button} from 'primeng/button';
import {PrimeIcons} from 'primeng/api';
import {UserService} from '../service/user.service';
import {NavigationService} from '../service/navigation.service';
import {Router} from '@angular/router';
import {RevizitService} from '../service/revizit.service';

@Component({
  selector: 'app-home',
  template: `
    <div class="home-container">
      <app-water-gallon class="gallon-display" [waterLevel]="waterLevel()"></app-water-gallon>
      <div class="home-description">
        <h1>Current water level is <span [class]="percentageStyle()">{{ waterLevel() }}%</span></h1>
        <h2>There are <b>{{ fullCount() }}</b> full gallons,</h2>
        <h2>and <b>{{ emptyCount() }}</b> empty ones.</h2>
        <i>As of {{ reportDate() | date }}</i>
        <div class="cta">
          <div class="cta-content">
            <span>Has anything changed since?</span>
            <p-button label="Make a Report!"
                      severity="primary"
                      [icon]="PrimeIcons.CHECK_CIRCLE"
                      (onClick)="onReportClicked()">
            </p-button>
          </div>
        </div>
        <div class="gallon-footer">

          <div class="gallon-collection">
            @for (g of fullGallons(); track g) {
              <svg class="full-gallon"
                   viewBox="0 0 512 512"
                   preserveAspectRatio="xMidYMid meet">
                <image
                  href="/gallon.svg"
                  x="0"
                  y="0"
                  width="512"
                  height="512"
                />
              </svg>
            }
          </div>
          <div class="gallon-collection">
            @for (g of emptyGallons(); track g) {
              <svg class="empty-gallon"
                   viewBox="0 0 512 512"
                   preserveAspectRatio="xMidYMid meet">
                <image
                  href="/gallon.svg"
                  x="0"
                  y="0"
                  width="512"
                  height="512"
                />
              </svg>
            }
          </div>
        </div>
      </div>
    </div>
  `,
  imports: [
    WaterGallonComponent,
    DatePipe,
    Button
  ],
  styles: `
    .home-container {
      display: flex;
      flex-direction: row;
      flex: 1;
      gap: 3rem;
      justify-content: center;
      align-self: center;
    }

    .gallon-display {
      flex: 2;
      transition: transform 0.2s ease, opacity 0.2s ease;
    }

    .gallon-display:hover {
      transform: translateY(4px);
    }


    /* Right: content column */
    .home-description {
      flex: 3;
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }

    /* Headings hierarchy */
    .home-description h1 {
      font-size: 4rem;
      font-weight: 600;
    }

    .home-description h2 {
      font-size: 3rem;
      font-weight: 400;
      margin: 0;
      color: #444;
    }

    /* Percentage emphasis */
    .home-description h1 span {
      font-weight: 700;
    }

    /* Timestamp */
    .home-description i {
      font-size: 0.9rem;
      color: #777;
    }

    /* CTA block */
    .cta {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 1.5rem;
      flex: 1;
    }

    .gallon-footer {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .gallon-collection {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      align-items: flex-end;
      flex-wrap: wrap;
    }

    /* Individual gallons */
    .full-gallon,
    .empty-gallon {
      height: 140px;
      transform: rotate(180deg);
      transition: transform 0.2s ease, opacity 0.2s ease;
    }

    .full-gallon:hover,
    .empty-gallon:hover {
      transform: rotate(180deg) translateY(-4px);
    }

    /* Empty visual treatment */
    .empty-gallon {
      filter: grayscale(100%) brightness(0.65);
      opacity: 0.8;
    }

    /* Percentage color states */
    .percentage-ok {
      color: #0b7a0b;
    }

    .percentage-mid {
      color: #9ab314;
    }

    .percentage-warn {
      color: #d57a00;
    }

    .percentage-critical {
      color: #d60000;
    }

    @media (max-width: 768px) {
      .home-container {
        flex-direction: column;
      }

      .home-description h1 {
        font-size: 1.5rem;
      }

      .home-description h2 {
        font-size: 1rem;
      }

      .full-gallon, .empty-gallon {
        height: 75px;
      }
    }
  `
})
export class Home implements OnInit {

  private readonly service = inject(RevizitService);

  private readonly userService = inject(UserService);
  private readonly navigationService = inject(NavigationService);
  private readonly router = inject(Router);

  waterLevel = computed(() => this.service.waterState().waterLevel);
  emptyCount = computed(() => this.service.waterState().emptyGallons);
  fullCount = computed(() => this.service.waterState().fullGallons);
  reportDate = computed(() => {
    const dateStr = this.service.waterState().reportedAt;
    return new Date(dateStr);
  });

  percentageStyle = computed<string>(() => {
    const level = this.waterLevel();
    if (level >= 75) {
      return 'percentage-ok';
    }

    if (level >= 50) {
      return 'percentage-mid';
    }

    if (level >= 25) {
      return 'percentage-warn';
    }

    return 'percentage-critical';
  });

  fullGallons = computed(() => {
    return Array.from({length: this.fullCount()}, (_, i) => i + 1);
  });

  emptyGallons = computed(() => {
    return Array.from({length: this.emptyCount()}, (_, i) => i + 1);
  });


  constructor() {
    this.service.loadWaterState();
  }

  ngOnInit(): void {
    this.navigationService.wantsToMakeAReport = false;
  }

  protected readonly PrimeIcons = PrimeIcons;

  onReportClicked() {
    this.router.navigateByUrl('/create-report');
  }
}
