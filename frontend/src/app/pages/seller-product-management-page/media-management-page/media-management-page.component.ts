import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialog,
} from '@angular/material/dialog';
import { finalize, firstValueFrom, Subject, takeUntil } from 'rxjs';
import { ConfirmComponent } from 'src/app/components/confirm/confirm.component';
import { Product } from 'src/app/shared/models/product.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { UserService } from 'src/app/shared/services/user.service';

@Component({
  selector: 'app-media-management-page',
  templateUrl: './media-management-page.component.html',
  styleUrls: ['./media-management-page.component.scss'],
})
export class MediaManagementPageComponent implements OnInit, OnDestroy {
  public form!: FormGroup;
  public invalidForm = false;
  public isLoading = false;
  public ownerName = '';

  private destroy$ = new Subject<void>();
  private imagesUploaded: string[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { product: Product },
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<MediaManagementPageComponent>,
    private productService: ProductService,
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private userService: UserService
  ) {
    this.form = this.formBuilder.group({
      name: [
        data.product.name,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(50),
        ],
      ],
      description: [
        data.product.description,
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(1000),
        ],
      ],
      price: [
        data.product.price,
        [Validators.required, Validators.pattern(/^\d+[.]{0,1}\d{0,2}$/)],
      ],
      quantity: [
        data.product.quantity,
        [Validators.required, Validators.pattern(/^\d+$/)],
      ],
      images: [],
    });
  }

  async ngOnInit() {
    this.form.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => (this.invalidForm = false));
    this.imagesUploaded = this.data.product.images;
    if (this.authService.profile?.role === 'admin') {
      this.ownerName = await firstValueFrom(
        this.userService.getUserNameById(this.data.product.userId)
      );
    } else {
      this.ownerName = 'You';
    }
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

  onRefreshImagesOrder(imageIds: string[]) {
    this.imagesUploaded = imageIds;
  }

  openConfirmDialog(product: Product): void {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: { message: 'You are deleting: ' + product.name },
    });

    dialogRef.afterClosed().subscribe((result) => {
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
        next: () => {
          this.dialogRef.close({
            type: 'success',
            message: `Product ${this.data.product.name} is deleted successfully.`,
          });
        },
        error: () => {
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
    this.form.controls['images'].setValue(this.imagesUploaded);
    this.productService
      .updateProduct(this.data.product.id, this.form.value)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: () => {
          this.dialogRef.close({
            type: 'success',
            message: `Product ${this.data.product.name} is updated successfully.`,
          });
        },
        error: () => {
          this.dialogRef.close({
            type: 'error',
            message: `Error happened when updating product ${this.data.product.name}.`,
          });
        },
      });
  }
}
