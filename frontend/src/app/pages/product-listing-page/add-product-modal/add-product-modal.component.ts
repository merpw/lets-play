import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
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

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private productService: ProductService,
    public dialogRef: MatDialogRef<AddProductModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', Validators.required],
      userId: [localStorage.getItem('userId'), Validators.required],
      quantity: ['', Validators.required],
      image: [''],
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

  onUpdate(): void {
    if (this.form.invalid) {
      this.invalidForm = true;
      return;
    }

    this.isLoading = true;
    console.log('updating product with the following values');
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
