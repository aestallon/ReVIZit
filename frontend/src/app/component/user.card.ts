import {Component, computed, input} from '@angular/core';
import {Avatar} from 'primeng/avatar';
import {Profile, ProfileThumbnail} from '../../api/revizit';

const isFullProfile = (it: Profile | ProfileThumbnail): it is Profile => (it as Profile).data !== undefined;

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
      <span>{{ name() }}</span>
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

  user = input.required<Profile | ProfileThumbnail>();
  name = computed(() => {
    const _user = this.user();
    return isFullProfile(_user) ? _user.data.name : _user.name;
  })
  initials = computed(() => {
    const name = this.name();
    if (name.length === 0 || 'anonymous' === name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  });
  pfp = computed(() => this.user().pfp);


}
