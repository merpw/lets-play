import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-media-management-page',
  templateUrl: './media-management-page.component.html',
  styleUrls: ['./media-management-page.component.scss'],
})
export class MediaManagementPageComponent implements OnInit, OnDestroy {
  public form!: FormGroup;
  public invalidForm = false;

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<MediaManagementPageComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.formBuilder.group({
      name: [data.product.name, Validators.required],
      description: [data.product.description, Validators.required],
      price: [data.product.price, Validators.required],
      quantity: [data.product.quantity, Validators.required],
      image: [data.product.image, Validators.required],
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
    // post request to update product here
    console.log('updating product with the following values');
    console.log(this.form.value);
    this.dialogRef.close();
  }
}
