import {Component, input, Pipe, PipeTransform} from '@angular/core';
import {SysLogEntry} from '../../../api/revizit';
import {Card} from 'primeng/card';
import {DatePipe} from '@angular/common';

@Pipe({
  name: 'logAction'
})
export class LogActionPipe  implements PipeTransform {

  transform(value: string, args?: any): string {
    switch (value) {
      case 'reportSubmitted': return 'Report Submitted';
      case 'reportsAccepted': return 'Report(s) Accepted';
      case 'reportsRejected': return 'Report Rejected';
      case 'defineState': return 'Admin Global State Definition';
      case 'createUsers': return 'Users Created';
      case 'deleteUser': return 'User Deleted';
      case 'changeUserRole': return 'User Role Changed';
      case 'resetUserPassword': return 'User Password Reset';
      case 'userLogIn': return 'User Logged In';
      default: return `Unclassified Event: ${value}`;
    }
  }
}

@Component({
  selector: 'app-log-card',
  template: `
    <p-card>
      <ng-template #title>{{ log().action | logAction }}</ng-template>
      <ng-template #subtitle>Performed by <strong>{{ log().user }}</strong>
        at {{ log().timestamp | date:'medium' }}
      </ng-template>
      <div class="card-content">
        @for (element of log().elements; track element.msg) {
          <p>{{ element.qualifier }} ({{ element.name }}): {{ element.msg }}</p>
        }
      </div>

    </p-card>
  `,
  imports: [
    Card,
    DatePipe,
    LogActionPipe
  ],
  styles: `
  `
})
export class LogCard {

  log = input.required<SysLogEntry>();
}
