import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
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
  @Input() images: any[] = [];
  @Input() onlyOne = false;
  @Output() upload = new EventEmitter<string>();
  @Output() delete = new EventEmitter<string>();
  @Output() newImagesOrder = new EventEmitter<string[]>();

  public uploadImageError = '';
  public imageUploaded: any[] = [];

  constructor(
    public mediaService: MediaService,
    private formValidationService: FormValidationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.images.forEach((imageId) => {
      this.imageUploaded.push({
        id: imageId,
        name: 'unnamed',
      });
    });
  }

  onDelete(deletedImage: any) {
    this.imageUploaded = this.imageUploaded.filter(
      (image) => image.id !== deletedImage.id
    );
    this.delete.emit(deletedImage.id);
  }

  openConfirmDialog(image: any): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: 'You are deleting: ' + image.name },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The confirm dialog was closed');
      console.log(result);
      if (result) {
        this.onDelete(image);
      }
    });
  }

  setMainImage(image: any) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: "Make this your product's main image?" },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The confirm dialog was closed');
      console.log(result);
      if (result) {
        this.imageUploaded = [
          image,
          ...this.imageUploaded.filter((val) => val.id !== image.id),
        ];
        this.newImagesOrder.emit(this.imageUploaded.map((image) => image.id));
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
          const imageId = resp.body;
          if (this.onlyOne) {
            this.imageUploaded = [];
          }
          this.imageUploaded.push({
            id: imageId,
            name: files.item(i)?.name || 'unnamed image',
          });
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
