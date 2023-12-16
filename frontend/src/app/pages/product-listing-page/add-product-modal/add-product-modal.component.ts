import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject, finalize, takeUntil } from 'rxjs';
import { FormValidationService } from 'src/app/shared/services/form-validation.service';
import { MediaService } from 'src/app/shared/services/media.service';
import { ProductService } from 'src/app/shared/services/product.service';

@Component({
  selector: 'app-add-product-modal',
  templateUrl: './add-product-modal.component.html',
  styleUrls: ['./add-product-modal.component.scss'],
})
export class AddProductModalComponent implements OnInit, OnDestroy {
  public isLoading = false;
  public form!: FormGroup;
  public invalidForm = false;
  public uploadAvatarError = '';
  private imagesUploaded: any[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private productService: ProductService,
    private mediaService: MediaService,
    private formValidationService: FormValidationService,
    public dialogRef: MatDialogRef<AddProductModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.formBuilder.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(50),
        ],
      ],
      description: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(1000),
        ],
      ],
      price: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      quantity: ['', [Validators.required, , Validators.pattern(/^\d+$/)]],
      images: [[]],
      userId: [localStorage.getItem('userId'), Validators.required],
    });
  }

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => (this.invalidForm = false));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onImageUpload(imageId: string) {
    this.imagesUploaded.push(imageId);
  }

  onImageDelete(imageId: string) {
    this.imagesUploaded = this.imagesUploaded.filter(
      (image) => image !== imageId
    );
  }

  onUpdate(): void {
    if (this.form.invalid) {
      this.invalidForm = true;
      return;
    }

    this.isLoading = true;
    console.log('updating product with the following values');
    this.form.controls['images'].setValue(this.imagesUploaded);
    console.log(this.form.value);
    this.productService
      .addProducts(this.form.value)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (resp) => {
          console.log(resp);
          this.dialogRef.close({
            type: 'success',
            message: `${this.form.controls['name'].value} has been added.`,
          });
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Add product went wrong. Please try again.';
          console.log(errorMessage);
          this.dialogRef.close({ type: 'error', message: errorMessage });
        },
      });
  }
}
