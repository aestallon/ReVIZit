import {Component, computed, input} from '@angular/core';
import {Avatar} from 'primeng/avatar';
import {Profile} from '../../api/revizit';

@Component({
  selector: 'app-user-card',
  imports: [
    Avatar
  ],
  template: `
    <div class="user-card">
      <p-avatar shape="circle"
                [image]="pfp()"
                [label]="!!pfp() ? undefined : initials()">
      </p-avatar>
      <span>{{ user().data.name }}</span>
    </div>
  `,
  styles: `
  .user-card {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: 1rem;
    font-weight: 700;
  }
  `
})
export class UserCard {

  user = input.required<Profile>();
  initials = computed(() => {
    const name = this.user()?.data?.name ?? '';
    if (name.length === 0) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  });
  pfp = computed(() => this.user()?.pfp);

}
