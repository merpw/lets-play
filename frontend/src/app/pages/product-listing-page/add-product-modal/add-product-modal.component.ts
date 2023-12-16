import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Subject, finalize, takeUntil } from 'rxjs';
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
  private imagesUploaded: string[] = [];

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private productService: ProductService,
    public dialogRef: MatDialogRef<AddProductModalComponent>
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
      price: [
        '',
        [Validators.required, Validators.pattern(/^\d+[.]{0,1}\d{0,2}$/)],
      ],
      quantity: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
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
    this.form.controls['images'].setValue(this.imagesUploaded);
    this.productService
      .addProducts(this.form.value)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.dialogRef.close({
            type: 'success',
            message: `${this.form.controls['name'].value} has been added.`,
          });
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Add product went wrong. Please try again.';
          this.dialogRef.close({ type: 'error', message: errorMessage });
        },
      });
  }
}
