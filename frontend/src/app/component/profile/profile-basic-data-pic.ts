import {Component, computed, inject, signal} from '@angular/core';
import {Button} from 'primeng/button';
import {MessageService, PrimeIcons} from 'primeng/api';
import {ImageCroppedEvent, ImageCropperComponent} from 'ngx-image-cropper';
import {Dialog} from 'primeng/dialog';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {FileSelectEvent, FileUpload} from 'primeng/fileupload';
import {Avatar} from 'primeng/avatar';
import {Image} from 'primeng/image';
import {UserService} from '../../service/user.service';
import {asErrorMsg} from '../../service/errors';

@Component({
  selector: 'app-profile-basic-data-pic',
  template: `
    @if (pfp()) {
      <p-image [src]="pfp()"
               width="300" height="300"
               imageClass="profile-pic"
               [preview]="true"
               alt="profile picture"/>
    } @else {
      <p-avatar [icon]="PrimeIcons.USER" size="xlarge" shape="circle"></p-avatar>
    }
    <p-fileupload [auto]="false"
                  mode="basic"
                  (onSelect)="onFicUpload($event)"
                  [chooseIcon]="PrimeIcons.UPLOAD"
                  accept="image/*"
                  maxFileSize="5000000">
    </p-fileupload>
    <p-dialog header="Crop & Upload" [visible]="cropperVisible()">
      <div class="upload-container">
        @if (imageFile()) {
          <div class="field-group">
            <image-cropper
              [imageFile]="imageFile() ?? undefined"
              [maintainAspectRatio]="true"
              [aspectRatio]="1"
              format="png"
              (imageCropped)="imageCropped($event)">
            </image-cropper>

          </div>
        }
        <div class="upload-controls">
          <p-button label="Upload Picture"
                    icon="pi pi-upload"
                    (onClick)="uploadPicture()"
                    [disabled]="!currentBlob">
          </p-button>
          <p-button label="Cancel"
                    severity="secondary"
                    [icon]="PrimeIcons.TIMES"
                    (onClick)="cropperVisible.set(false)">
          </p-button>
        </div>
      </div>

    </p-dialog>
  `,
  imports: [
    Button,
    ImageCropperComponent,
    Dialog,
    Avatar,
    FileUpload,
    Image
  ],
  styles: `
    :host ::ng-deep .profile-pic {
      border-radius: 50%;
    }

    :host ::ng-deep .p-image-preview-mask {
      border-radius: 50%;
    }
  `
})
export class ProfileBasicDataPic {

  protected readonly PrimeIcons = PrimeIcons;

  userService = inject(UserService);
  messageService = inject(MessageService);
  sanitizer = inject(DomSanitizer);
  pfp = computed(() => this.userService.profile()?.pfp);

  imageFile = signal<File | null>(null);
  croppedImage: SafeUrl = '';
  currentBlob: Blob | null | undefined = null;

  cropperVisible = signal(false);

  imageCropped(event: ImageCroppedEvent) {
    if (event.objectUrl != null) {
      this.croppedImage = this.sanitizer.bypassSecurityTrustUrl(event.objectUrl);
    }
    this.currentBlob = event.blob;
  }

  onFicUpload(event: FileSelectEvent) {
    this.imageFile.set(event.files[0]);
    this.cropperVisible.set(true);
  }

  async uploadPicture() {
    if (!this.currentBlob) return;
    this.userService.updatePfp(this.currentBlob)
      .then(() => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Profile picture updated',
          life: 3000
        });
        this.imageFile.set(null);
        this.croppedImage = '';
        this.currentBlob = null;
        this.cropperVisible.set(false);
      })
      .catch(err => this.messageService.add(asErrorMsg(err, 'Failed to update profile picture!')));
  }
}
