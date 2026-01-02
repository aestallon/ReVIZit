import {Component, computed, input, output} from '@angular/core';
import {Button} from 'primeng/button';
import {Avatar} from 'primeng/avatar';
import {Profile} from '../../api/revizit';

@Component({
  selector: 'app-user-btn',
  template: `
    <p-button (onClick)="onClick.emit($event)" variant="text">
      <p-avatar shape="circle"
                [image]="pfp()"
                [label]="pfp() ? undefined : initials()">
      </p-avatar>
      <span>{{ profile()?.data?.name ?? 'anonymous' }}</span>
    </p-button>
  `,
  imports: [
    Button,
    Avatar
  ],
  styles: `
  `
})
export class UserBtn {

  profile = input<Profile>();
  initials = computed(() => {
    const name = this.profile()?.data?.name ?? '';
    if (name.length === 0) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  });
  pfp = computed(() => this.profile()?.pfp);
  onClick = output<MouseEvent>()
}
