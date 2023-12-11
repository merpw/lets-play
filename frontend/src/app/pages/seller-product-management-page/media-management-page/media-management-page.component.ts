import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialog,
} from '@angular/material/dialog';
import { finalize, Subject, takeUntil } from 'rxjs';
import { ConfirmComponent } from 'src/app/components/confirm/confirm.component';
import { ProductService } from 'src/app/shared/services/product.service';

@Component({
  selector: 'app-media-management-page',
  templateUrl: './media-management-page.component.html',
  styleUrls: ['./media-management-page.component.scss'],
})
export class MediaManagementPageComponent implements OnInit, OnDestroy {
  public form!: FormGroup;
  public invalidForm = false;
  public isLoading = false;

  private destroy$ = new Subject<void>();
  private imagesUploaded: any[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<MediaManagementPageComponent>,
    private productService: ProductService,
    private formBuilder: FormBuilder
  ) {
    this.form = this.formBuilder.group({
      name: [data.product.name, Validators.required],
      description: [data.product.description, Validators.required],
      price: [data.product.price, Validators.required],
      quantity: [data.product.quantity, Validators.required],
      images: [],
    });
  }

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => (this.invalidForm = false));
    this.imagesUploaded = this.data.product.images;
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

  openConfirmDialog(product: any): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: 'You are deleting: ' + product.name },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The confirm dialog was closed');
      console.log(result);
      if (result) {
        this.onDelete();
      }
    });
  }

  onDelete(): void {
    this.isLoading = true;
    this.productService
      .deleteProduct(this.data.product.id)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (resp) => {
          console.log(resp);
          this.dialogRef.close({
            type: 'success',
            message: `Product ${this.data.product.name} is deleted successfully.`,
          });
        },
        error: (err) => {
          console.log(err);
          this.dialogRef.close({
            type: 'error',
            message: `Error happened when deleting product ${this.data.product.name}.`,
          });
        },
      });
  }

  onUpdate(): void {
    this.isLoading = true;
    if (this.form.invalid) {
      this.invalidForm = true;
      this.isLoading = false;
      return;
    }
    // post request to update product here
    console.log('updating product with the following values');
    this.form.controls['images'].setValue(this.imagesUploaded);
    console.log(this.form.value);
    this.productService
      .updateProduct(this.data.product.id, this.form.value)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (resp) => {
          console.log(resp);
          this.dialogRef.close({
            type: 'success',
            message: `Product ${this.data.product.name} is updated successfully.`,
          });
        },
        error: (err) => {
          console.log(err);
          this.dialogRef.close({
            type: 'error',
            message: `Error happened when updating product ${this.data.product.name}.`,
          });
        },
      });
  }
}
