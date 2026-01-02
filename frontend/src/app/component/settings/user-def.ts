import {AfterViewInit, Component, computed, inject, signal} from '@angular/core';
import {UserAdminService} from '../../service/user-admin.service';
import {MessageService, PrimeIcons} from 'primeng/api';
import {asErrorMsg} from '../../service/errors';
import {TableModule} from 'primeng/table';
import {Message} from 'primeng/message';
import {Button} from 'primeng/button';
import {UserService} from '../../service/user.service';
import {Profile} from '../../../api/revizit';
import {Avatar} from 'primeng/avatar';
import {UserCard} from '../user.card';

@Component({
  selector: 'app-user-def',
  template: `
    <p-table [value]="users()"
             dataKey="username"
             [rowHover]="true"
             [loading]="loading()">
      <ng-template pTemplate="caption">
        <h2>Active Users</h2>
        <div class="table-info">
          <p-message severity="info" [icon]="PrimeIcons.INFO_CIRCLE">
            <p>Lorem ipsum dolor sit amet</p>
          </p-message>
        </div>
        <span style="flex: 1;"></span>
        <p-button label="Import Users"
                  [icon]="PrimeIcons.PLUS_CIRCLE"
                  severity="primary"
                  [loading]="loading()"
                  [disabled]="unavailable()"
                  (onClick)="importUsers()">
        </p-button>
      </ng-template>

      <ng-template pTemplate="header">
        <tr>
          <th>Name</th>
          <th>Username</th>
          <th>Admin?</th>
          <th>E-mail</th>
          <th>Actions</th>
        </tr>
      </ng-template>

      <ng-template pTemplate="body" let-user>
        <tr>
          <td>
            <app-user-card [user]="user"></app-user-card>
          </td>
          <td>{{ user.username }}</td>
          <td>{{ user.isAdmin ? 'Admin' : '' }}</td>
          <td>{{ user.data.email }}</td>
          <td>
            @if (user.isAdmin && !isMyUser(user)) {
              <p-button label="Demote Admin"></p-button>
            } @else if (!isMyUser(user)) {
              <p-button label="Promote Admin"></p-button>
            }
            @if (!isMyUser(user)) {
              <p-button label="Delete"></p-button>
            }
          </td>
        </tr>
      </ng-template>
    </p-table>
  `,
  imports: [
    TableModule,
    Message,
    Button,
    Avatar,
    UserCard
  ],
  styles: `

  `
})
export class UserDef implements AfterViewInit {

  protected readonly PrimeIcons = PrimeIcons;

  service = inject(UserAdminService);
  msg = inject(MessageService);
  userService = inject(UserService);

  myUsername = computed(() => this.userService.profile()?.username!);
  loading = signal(true);
  unavailable = signal(false);
  users = computed(() => this.service.users());

  ngAfterViewInit() {
    this.service.fetchUsers()
      .then(() => {
        this.loading.set(false);
      })
      .catch(err => {
        this.loading.set(false);
        this.unavailable.set(true);
        this.msg.add(asErrorMsg(err, 'Failed to load user data'));
      });
  }

  importUsers() {

  }

  isMyUser(p: Profile) {
    return p.username === this.myUsername();
  }

  initials(p : Profile) {
    const name = p.data.name ?? '';
    if (name.length === 0) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  }
}
