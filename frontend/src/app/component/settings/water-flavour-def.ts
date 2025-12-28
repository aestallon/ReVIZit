import {Component, computed, inject, signal, viewChild} from '@angular/core';
import {RevizitService} from '../../service/revizit.service';
import {TableModule} from 'primeng/table';
import {WaterFlavourDto} from '../../../api/revizit';
import {Button} from 'primeng/button';
import {MessageService, PrimeIcons} from 'primeng/api';
import {Message} from 'primeng/message';
import {Popover} from 'primeng/popover';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {asCustomErrorMsg} from '../../service/errors';

@Component({
  selector: 'app-water-flavour-def',
  standalone: true,
  template: `
    <div class="flavour-container">
      <p-table [value]="activeFlavours()"
               dataKey="id"
               [rowHover]="true"
               [loading]="loading()">
        <ng-template pTemplate="caption">
          <h2>Active Flavours</h2>
          <div class="table-info">
            <p-message severity="info" icon="pi pi-info-circle">
              <p>You may manage the currently active flavours here. These are the flavours from
                which reporters can pick when submitting a <em>Ballon Change</em> report.</p>
              <p>Flavours which have never been submitted through reports can be freely deleted.</p>
              <p>Flavours which have been submitted through reports before may be archived only
                (Users cannot select an archived flavour when submitting a report).</p>
              <p><em>The current flavour cannot be archived.</em></p>
            </p-message>
            <span style="flex: 1;"></span>
            <p-button label="Create New Flavour"
                      [icon]="PrimeIcons.PLUS_CIRCLE"
                      severity="primary"
                      [loading]="loading()"
                      [disabled]="unavailable()"
                      (onClick)="onCreate($event)">
            </p-button>
          </div>
        </ng-template>
        <ng-template pTemplate="header">
          <tr>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-flavour>
          <tr>
            <td>{{ flavour.name }}</td>
            <td>
              @if (isUnusued(flavour)) {
                <p-button label="Delete"
                          class="control-btn"
                          [icon]="PrimeIcons.TRASH"
                          severity="danger"
                          [loading]="loading()"
                          [disabled]="unavailable()"
                          (onClick)="onDelete(flavour)">
                </p-button>
              } @else if (flavour.id !== currFlavour()) {
                <p-button label="Archive"
                          class="control-btn"
                          [icon]="PrimeIcons.KEY"
                          severity="warn"
                          [loading]="loading()"
                          [disabled]="unavailable()"
                          (onClick)="onArchive(flavour)">
                </p-button>
              }
              <p-button label="Rename"
                        [icon]="PrimeIcons.PENCIL"
                        severity="info"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        (onClick)="onRename($event, flavour)">
              </p-button>
            </td>
          </tr>

        </ng-template>
      </p-table>

      <p-table [value]="inactiveFlavours()"
               dataKey="id"
               [rowHover]="true"
               [loading]="loading()">
        <ng-template pTemplate="caption">
          <h2>Archived Flavours</h2>
          <p-message severity="info" icon="pi pi-info-circle">
            <p>You can manage the archived flavours here.</p>
            <p>Archived flavours are currently not available for selection when submitting a report,
              but had been used in reports in the past. You can freely <i class="pi pi-pencil"></i>
              rename or <i class="pi pi-refresh"></i> restore them.</p>
          </p-message>
        </ng-template>
        <ng-template pTemplate="header">
          <tr>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-flavour>
          <tr>
            <td>{{ flavour.name }}</td>
            <td>
              <p-button label="Restore"
                        class="control-btn"
                        [icon]="PrimeIcons.REFRESH"
                        severity="primary"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        (onClick)="onActivate(flavour)">
              </p-button>
              <p-button label="Rename"
                        [icon]="PrimeIcons.PENCIL"
                        severity="info"
                        [loading]="loading()"
                        [disabled]="unavailable()"
                        (onClick)="onRename($event, flavour)">
              </p-button>
            </td>
          </tr>

        </ng-template>
      </p-table>
      <p-popover #op (onHide)="targetFlavour.set(null)">
        <div class="flavour-rename-form">
          <p-float-label>
            <input pInputText
                   id="new_name"
                   type="text"
                   [(ngModel)]="newName"
                   (keyup.enter)="enactChange()"/>
            <label for="on_new_name">New Name</label>
          </p-float-label>
          <p-button label="Save"
                    [icon]="PrimeIcons.SAVE"
                    [loading]="loading()"
                    [disabled]="unavailable()"
                    (onClick)="enactChange()">
          </p-button>
        </div>
      </p-popover>
    </div>
  `,
  imports: [
    TableModule,
    Button,
    Message,
    Popover,
    FloatLabel,
    InputText,
    FormsModule
  ],
  styles: `
    .table-info {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
    }

    .control-btn {
      margin-right: 1em;
    }
  `
})
export class WaterFlavourDef {

  service = inject(RevizitService);
  messageService = inject(MessageService);
  activeFlavours = computed(() => this.service.allWaterFlavours().filter(it => !it.inactive));
  inactiveFlavours = computed(() => this.service.allWaterFlavours().filter(it => it.inactive));

  currFlavour = computed(() => this.service.waterState().flavour.id);
  loading = signal(true);
  unavailable = signal(false);

  targetFlavour = signal<WaterFlavourDto | null>(null);
  newName = signal('');

  op = viewChild.required<Popover>('op');

  constructor() {
    this.service.loadAllWaterFlavours()
      .then(() => this.loading.set(false))
      .catch((err) => {
        this.loading.set(false);
        this.unavailable.set(true);
        this.messageService.add(asCustomErrorMsg(err, 'Failed to load flavour data'));
      });
  }

  isUnusued(flavour: WaterFlavourDto) {
    return !this.service.usedWaterFlavours().has(flavour.id);
  }

  onDelete(flavour: WaterFlavourDto) {
    this.archiveOrDelete(flavour, 'delete');
  }

  onArchive(flavour: WaterFlavourDto) {
    this.archiveOrDelete(flavour, 'archive');
  }

  archiveOrDelete(flavour: WaterFlavourDto, arg: 'archive' | 'delete') {
    this.loading.set(true);
    this.service.deleteFlavour(flavour)
      .then(() => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: `Flavour ${arg}d successfully!`,
          life: 3000
        });
      })
      .catch(err => {
        this.loading.set(false);
        this.messageService.add(asCustomErrorMsg(err, `Failed to ${arg} flavour`));
      });
  }

  onActivate(flavour: WaterFlavourDto) {
    this.loading.set(true);
    this.service.activateFlavour(flavour)
      .then(() => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Flavour enabled successfully!',
          life: 3000
        });
      })
      .catch(err => {
        this.loading.set(false);
        this.messageService.add(asCustomErrorMsg(err, 'Failed to enable flavour'));
      });
  }

  onRename(event: MouseEvent, flavour: WaterFlavourDto) {
    const tf = this.targetFlavour();
    if (tf?.id === flavour.id) {
      this.op().hide();
      this.targetFlavour.set(null);
    } else {
      this.targetFlavour.set(flavour);
      this.newName.set(flavour.name);
      this.op().show(event);
      if (this.op().container) {
        this.op().align();
      }
    }
  }

  enactChange() {
    const f = this.targetFlavour();
    const newName = this.newName();
    this.op().hide();
    this.targetFlavour.set(null);
    this.newName.set('');
    this.loading.set(true);
    if (f) {
      this.service.renameFlavour({...f, name: newName}).then(() => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Flavour renamed successfully!',
          life: 3000
        });
      })
        .catch(err => {
          this.loading.set(false);
          this.messageService.add(asCustomErrorMsg(err, 'Failed to rename flavour'));
        });
    } else {
      this.service.createFlavour(newName).then(() => {
        this.loading.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Flavour created successfully!',
          life: 3000
        });
      })
        .catch(err => {
          this.loading.set(false);
          this.messageService.add(asCustomErrorMsg(err, 'Failed to create flavour'));
        })
    }
  }

  onCreate(event: MouseEvent) {
    this.newName.set('');
    this.targetFlavour.set(null);
    this.op().show(event);
    if (this.op().container) {
      this.op().align();
    }
  }

  protected readonly PrimeIcons = PrimeIcons;
  protected readonly onclick = onclick;
}
