import {
  AfterViewInit,
  Component,
  computed,
  ElementRef,
  HostListener, input,
  model,
  signal, viewChild,
  ViewChild
} from '@angular/core';
import {NgClass} from '@angular/common';

const SVG_HEIGHT = 512;
const SVG_WIDTH = 512;

@Component({
  selector: 'app-water-gallon',
  standalone: true,
  imports: [
    NgClass
  ],
  template: `
    <svg #ballonSvg
         [ngClass]="flipped() ? 'gallon-svg gallon-flipped': 'gallon-svg'"
         [style]="editable() ? 'cursor: pointer;' : 'cursor: default;'"
         viewBox="0 0 512 512"
         preserveAspectRatio="xMidYMid meet"
    >
      <defs>
        <clipPath [attr.id]="clipPathId">
          <rect
            x="0"
            [attr.y]="waterClipY()"
            width="512"
            [attr.height]="waterClipHeight()"
          />
        </clipPath>
      </defs>

      <!-- Greyed-out background -->
      <image
        href="/gallon.svg"
        x="0"
        y="0"
        width="512"
        height="512"
        class="gallon-grey"
      />

      <!-- Bright (water-filled) part -->
      <image
        href="/gallon.svg"
        x="0"
        y="0"
        width="512"
        height="512"
        [attr.clip-path]="'url(#' + clipPathId + ')'"
        class="gallon-water"
      />
    </svg>
  `,
  styles: `
    :host {
      display: block;
      height: 100%;
      max-height: 100%;
      user-select: none;
      touch-action: none;
    }


    .gallon-svg {
      width: 100%;
      height: 100%;
      display: block;
    }


    .gallon-water {
      filter: none;
    }

    .gallon-flipped {
      transform: rotate(180deg);
    }
  `,
})
export class WaterGallonComponent /* implements AfterViewInit*/ {

  private static nextId = 0;
  readonly clipPathId = `waterClip-${WaterGallonComponent.nextId++}`;

  flipped = input<boolean>(false);

  waterLevel = model.required<number>();
  editable = input<boolean>(false);

  svg = viewChild.required<ElementRef<SVGElement>>('ballonSvg');

  waterClipHeight = computed(() =>
    (this.waterLevel() / 100) * SVG_HEIGHT
  );

  waterClipY = computed(() =>
    SVG_HEIGHT - this.waterClipHeight()
  );


  private onDrag(clientY: number) {
    const rect = this.svg().nativeElement.getBoundingClientRect();

    // Calculate relative position in SVG
    const relativeY = (clientY - rect.top) / rect.height * SVG_HEIGHT;

    // Convert SVG Y coordinate to percentage
    const minY = 0;
    const maxY = 512;
    const clampedY = Math.max(minY, Math.min(maxY, relativeY));
    const percentage = 100 * (1 - (clampedY - minY) / (maxY - minY));

    // Round to nearest integer
    const roundedPercentage = Math.round(percentage);

    if (roundedPercentage !== this.waterLevel()) {
      this.waterLevel.set(roundedPercentage);
    }
  }

  @HostListener('click', ['$event'])
  onClick(event: MouseEvent) {
    if (!this.editable()) {
      return;
    }

    const svgElement = (event.target as Element).closest('svg');
    if (svgElement) {
      this.onDrag(event.clientY);
    }
  }

}
