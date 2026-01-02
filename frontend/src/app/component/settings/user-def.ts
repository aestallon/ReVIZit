import {AfterViewInit, Component, computed, inject, signal, viewChild} from '@angular/core';
import {UserAdminService} from '../../service/user-admin.service';
import {MessageService, PrimeIcons} from 'primeng/api';
import {asErrorMsg} from '../../service/errors';
import {TableModule} from 'primeng/table';
import {Message} from 'primeng/message';
import {Button} from 'primeng/button';
import {UserService} from '../../service/user.service';
import {Profile} from '../../../api/revizit';
import {UserCard} from '../user.card';
import {FileSelectEvent, FileUpload} from 'primeng/fileupload';

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
          <p-message severity="info" [icon]="PrimeIcons.INFO_CIRCLE" [closable]="true">
            <h3>Importing Users</h3>
            <p>You can <i [class]="PrimeIcons.PLUS_CIRCLE"></i><strong> Import Users</strong> by
              clicking the button below and uploading a semicolon (<strong><code>;</code></strong>)
              delimited <strong>CSV</strong> file.</p>
            <p>The CSV file should consists of the following columns:
              <code>username;email;admin</code></p>
            <p>The respective columns should contain the following:</p>
            <ol>
              <li><code>username</code>: The desired username of the new user. Must be shorter than
                256 characters.
              </li>
              <li><code>email</code>: The e-mail address of the new user, used for notifications and
                account recovery mails. <em>If not known, can be left empty: users can freely change
                  their e-mail address at any point.</em></li>
              <li><code>admin</code>: Whether the new user should be imported as an
                <strong>admin</strong>. Values such as <code>true</code>, <code>TRUE</code> and
                <code>Y</code> and their different casings are accepted values to signal the admin
                rank of the new users. All other values are treated as <strong>non-admins</strong>.
              </li>
            </ol>
            <p>The following snippet shows a valid user import CSV:</p>
            <pre><code>
              username;email;admin
              alice;alice&#64;acme.org;true
              bob;;false
            </code></pre>
            <p>As a result, the user <code>alice</code> would end up as a new admin, and
              <code>bob</code> as a simple user, with his e-mail address left blank.</p>
            <p><em>The importing mechanism skips the rows associated with already existing users of
              the application.</em></p>
            <p>The users are created with the <strong>default password</strong> configured in the
              system. <em>If you are not sure about its value, please reach out to the
                operators.</em></p>
            <h3>Modifying Users</h3>
            <p>The following operations are available for the active users of the application:</p>
            <ul>
              <li><i [class]="PrimeIcons.ARROW_CIRCLE_DOWN"></i><strong> Demote Admin</strong>:
                Demotes an <em>admin</em> rank user to a simple user. <em>You cannot demote
                  yourself.</em>
              </li>
              <li><i [class]="PrimeIcons.ARROW_CIRCLE_UP"></i><strong> Promote Admin</strong>:
                Promotes a plain user into administrator rank.
              </li>
              <li><i [class]="PrimeIcons.LOCK_OPEN"></i><strong> Reset Password</strong>: Resets the
                user's password to the <strong>default password</strong> configured for the system.
                <em>If you are uncertain about its value, please reach out to the operators. As you
                  have demonstrated your ability to log-in to the system, this operation is not
                  available for yourself.</em>
              </li>
              <li><i [class]="PrimeIcons.TRASH"></i><strong> Delete</strong>: Permanently deletes
                the given user. Any remnant data shall be anonymised. <em>This operation is not
                  available for yourself. You may delete your account from the
                  <strong>Profile</strong> menu.</em>
              </li>
            </ul>
            <p>All changes take effect immediately, with no confirmation prompt.</p>
          </p-message>
        </div>
        <span style="flex: 1;"></span>
        <p-fileupload #userUploader
                      chooseLabel="Import Users"
                      [chooseIcon]="PrimeIcons.PLUS_CIRCLE"
                      mode="basic"
                      [auto]="false"
                      [disabled]="loading() || unavailable()"
                      accept=".csv"
                      (onSelect)="importUsers($event)">
        </p-fileupload>
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
          <td style="display: flex;">
            @if (user.isAdmin && !isMyUser(user)) {
              <p-button class="user-admin-action"
                        label="Demote Admin"
                        severity="warn"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        [icon]="PrimeIcons.ARROW_CIRCLE_DOWN"
                        (onClick)="onRoleChangeClicked(user)">
              </p-button>
            } @else if (!isMyUser(user)) {
              <p-button class="user-admin-action"
                        label="Promote Admin"
                        severity="info"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        [icon]="PrimeIcons.ARROW_CIRCLE_UP"
                        (onClick)="onRoleChangeClicked(user)">
              </p-button>
            }

            @if (!user.isAdmin && !isMyUser(user)) {
              <p-button class="user-admin-action"
                        label="Reset Password"
                        severity="warn"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        [icon]="PrimeIcons.LOCK_OPEN"
                        (onClick)="onUserPasswordResetClicked(user)">
              </p-button>
            }
            <span style="flex: 1"></span>
            @if (!isMyUser(user)) {
              <p-button class="user-admin-action"
                        label="Delete"
                        severity="danger"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        [icon]="PrimeIcons.TRASH"
                        (onClick)="onDeleteClicked(user)">
              </p-button>
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
    UserCard,
    FileUpload
  ],
  styles: `
    .user-admin-action {
      margin-right: 1em;
    }

    @media (max-width: 768px) {
      .user-admin-action {
        margin-right: unset;
        margin-bottom: 1em;
      }

    }
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

  userUploader = viewChild.required<FileUpload>('userUploader');

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

  importUsers(e: FileSelectEvent) {
    this.loading.set(true);
    this.service.createUsers(e.files[0])
      .then(() => {
        this.loading.set(false);
        this.msg.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Users imported successfully!',
          life: 3000
        });
        this.userUploader().clear();
      })
      .catch(err => {
        this.msg.add(asErrorMsg(err, 'Failed to import users'));
      });
  }

  isMyUser(p: Profile) {
    return p.username === this.myUsername();
  }

  initials(p: Profile) {
    const name = p.data.name ?? '';
    if (name.length === 0) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  }

  onRoleChangeClicked(p: Profile) {
    this.loading.set(true);
    this.service.promoteOrDemoteUser(p.username)
      .then(() => {
        this.loading.set(false);
        this.msg.add({
          severity: 'success',
          summary: 'Success',
          detail: 'User role changed successfully!',
          life: 3000
        });
      })
      .catch(err => {
        this.loading.set(false);
        this.msg.add(asErrorMsg(err, 'Failed to change user role'));
      });
  }

  onDeleteClicked(p: Profile) {
    this.loading.set(true);
    this.service.deleteUser(p.username)
      .then(() => {
        this.loading.set(false);
        this.msg.add({
          severity: 'success',
          summary: 'Success',
          detail: 'User deleted successfully!',
          life: 3000
        });
      })
      .catch(err => {
        this.loading.set(false);
        this.msg.add(asErrorMsg(err, 'Failed to delete user'));
      })
  }

  onUserPasswordResetClicked(p: Profile) {
    this.loading.set(true);
    this.service.resetUserPassword(p.username)
      .then(() => {
        this.loading.set(false);
        this.msg.add({
          severity: 'success',
          summary: 'Success',
          detail: `${p.data.name}'s password reset successfully!`,
          life: 3000
        });
      })
      .catch(err => {
        this.loading.set(false);
        this.msg.add(asErrorMsg(err, `Failed to reset ${p.data.name} password`));
      });
  }
}
