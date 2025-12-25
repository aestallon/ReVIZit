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

const SVG_HEIGHT = 512;
const SVG_WIDTH = 512;

@Component({
  selector: 'app-water-gallon',
  standalone: true,
  imports: [],
  template: `
    <svg #ballonSvg
         class="gallon-svg"
         [style]="editable() ? 'cursor: pointer;' : 'cursor: default;'"
         viewBox="0 0 512 512"
         preserveAspectRatio="xMidYMid meet"
    >
      <defs>
        <clipPath id="waterClip">
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
        clip-path="url(#waterClip)"
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

    .gallon-grey {
      filter: grayscale(100%) brightness(0.65);
    }

    .gallon-water {
      filter: none;
    }
  `,
})
export class WaterGallonComponent /* implements AfterViewInit*/ {
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
