import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { FormValidationService } from 'src/app/shared/services/form-validation.service';
import { MediaService } from 'src/app/shared/services/media.service';
import { ConfirmComponent } from '../confirm/confirm.component';

@Component({
  selector: 'app-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.scss'],
})
export class ImageUploadComponent implements OnInit {
  @Input() images: string[] = [];
  @Input() onlyOne = false;
  @Output() upload = new EventEmitter<string>();
  @Output() delete = new EventEmitter<string>();
  @Output() newImagesOrder = new EventEmitter<string[]>();

  public uploadImageError = '';
  public imageUploaded: string[] = [];

  constructor(
    public mediaService: MediaService,
    private formValidationService: FormValidationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.images.forEach((imageId) => {
      this.imageUploaded.push(imageId);
    });
  }

  onDelete(deletedImageId: string) {
    this.imageUploaded = this.imageUploaded.filter(
      (imageId) => imageId !== deletedImageId
    );
    this.delete.emit(deletedImageId);
  }

  openConfirmDialog(image: string): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: 'Please confirm to delete the image.' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.onDelete(image);
      }
    });
  }

  setMainImage(image: string) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: "Make this your product's main image?" },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.imageUploaded = [
          image,
          ...this.imageUploaded.filter((val) => val !== image),
        ];
        this.newImagesOrder.emit(this.imageUploaded);
      }
    });
  }

  onImageUpload(files: FileList | null) {
    this.uploadImageError = '';
    if (!files) {
      return;
    }

    for (let i = 0; i < files.length; i++) {
      const file = files.item(i);
      const invalidAvatarUpload =
        this.formValidationService.validateImageUpload(file);
      if (invalidAvatarUpload) {
        this.uploadImageError = invalidAvatarUpload;
        return;
      }
    }

    for (let i = 0; i < files.length; i++) {
      this.mediaService.uploadMedia(<File>files.item(i)).subscribe({
        next: (resp) => {
          const imageId: string = resp.body || '';
          if (this.onlyOne) {
            this.imageUploaded = [];
          }
          this.imageUploaded.push(imageId);
          this.upload.emit(imageId);
        },
        error: (err) => {
          console.log(err);
          this.delete.emit('error');
          this.uploadImageError = 'Upload image error';
        },
      });
    }
  }
}
