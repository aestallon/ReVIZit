import { Component } from '@angular/core';

@Component({
  selector: 'app-water-background',
  standalone: true,
  template: `
    <div class="water-container">
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
      <div class="wave wave4"></div>
    </div>
  `,
  styles: `
    .water-container {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      background: linear-gradient(180deg, #f0f7ff 0%, #e0f0ff 100%);
      z-index: -1;
      overflow: hidden;
    }

    .wave {
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 200px;
      background: url('data:image/svg+xml;utf8,<svg viewBox="0 0 800 200" xmlns="http://www.w3.org/2000/svg"><path d="M 0 100 Q 200 50 400 100 Q 600 150 800 100 V 200 H 0 Z" fill="rgba(33, 150, 243, 0.1)"/></svg>') repeat-x;
      transform-origin: center bottom;
    }

    .wave1 {
      z-index: 4;
      opacity: 0.5;
    }

    .wave2 {
      z-index: 3;
      opacity: 0.3;
      bottom: 5px;
      transform: scaleX(-1);
    }

    .wave3 {
      z-index: 2;
      opacity: 0.2;
      bottom: 10px;
      transform: translateX(-10%);
    }

    .wave4 {
      z-index: 1;
      opacity: 0.1;
      bottom: 15px;
      transform: scaleX(-1) translateX(-20%);
    }
  `
})
export class WaterBackground {}
