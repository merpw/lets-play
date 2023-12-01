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
  @Input() imageId: string = '';
  @Output() upload = new EventEmitter<string>();
  @Output() delete = new EventEmitter<void>();
  @ViewChild('avatarInput') avatarInput!: ElementRef;

  public uploadImageError = '';
  public imagePreviewSrc = '';
  public imageName = '';

  constructor(
    private formValidationService: FormValidationService,
    private mediaService: MediaService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    if (this.imageId) {
      this.imagePreviewSrc =
        this.mediaService.mediaApiBaseURL + '/' + this.imageId;
    }
  }

  onDelete() {
    this.imageName = '';
    this.imagePreviewSrc = '';
    this.avatarInput.nativeElement.value = null;
    this.delete.emit();
  }

  openConfirmDialog(imageName: string): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: 'You are deleting: ' + imageName },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The confirm dialog was closed');
      console.log(result);
      if (result) {
        this.onDelete();
      }
    });
  }

  onImageUpload(files: FileList | null) {
    this.uploadImageError = '';
    const file = files?.item(0);
    const invalidAvatarUpload =
      this.formValidationService.validateImageUpload(file);
    if (invalidAvatarUpload) {
      this.uploadImageError = invalidAvatarUpload;
      return;
    }

    this.mediaService.uploadMedia(<File>file).subscribe({
      next: (resp) => {
        const imageId = resp.body;
        this.imageName = file?.name || 'unnamed image uploaded';
        console.log(this.imageName + ' successful, media id ' + imageId);
        this.imagePreviewSrc =
          this.mediaService.mediaApiBaseURL + '/' + imageId;
        this.upload.emit(imageId);
      },
      error: (err) => {
        console.log(err);
        this.delete.emit();
        this.uploadImageError = 'Upload image error';
      },
    });
  }
}
