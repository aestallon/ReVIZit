import {inject, Injectable, signal} from '@angular/core';
import {Profile, UserManagementService} from '../../api/revizit';
import {lastValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserAdminService {

  service = inject(UserManagementService);
  users = signal<Array<Profile>>([]);

  public async fetchUsers() {
    const result = await lastValueFrom(this.service.getAllUsers());
    this.users.set(result);
  }

  public async deleteUser(username: string) {
    await lastValueFrom(this.service.deleteUser({username}));
    this.users.update(us => {
      const newUsers = us.filter(it => it.username !== username);
      return [...newUsers];
    })
  }

  public async resetUserPassword(username: string) {
    await lastValueFrom(this.service.resetUserPassword({username}));
  }

  public async createUsers(file: File) {
    await lastValueFrom(this.service.createUsers(file));
    await this.fetchUsers();
  }

  public async promoteOrDemoteUser(username: string) {
    const user = await lastValueFrom(this.service.flipUserRole({username}));
    this.users.update(us => {
      const idx = us.findIndex(it => username === it.username);
      if (idx < 0) {
        return [...us, user];
      } else {
        const newUsers = [...us];
        newUsers[idx] = user;
        return newUsers;
      }
    })
  }

}
