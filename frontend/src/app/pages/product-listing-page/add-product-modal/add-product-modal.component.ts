import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject, finalize, takeUntil } from 'rxjs';
import { MediaManagementPageComponent } from '../../seller-product-management-page/media-management-page/media-management-page.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-add-product-modal',
  templateUrl: './add-product-modal.component.html',
  styleUrls: ['./add-product-modal.component.scss'],
})
export class AddProductModalComponent implements OnInit, OnDestroy {
  private addProductUrl = 'api/products/add';
  public form!: FormGroup;
  public invalidForm = false;

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    public dialogRef: MatDialogRef<AddProductModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: ['', Validators.required],
      userId: [localStorage.getItem('userId'), Validators.required],
      // quantity: ['', Validators.required],
      // image: ['', Validators.required],
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
    this.http
      .post(this.addProductUrl, this.form.value, {
        withCredentials: true,
        observe: 'response',
        responseType: 'text',
      })
      .pipe(finalize(() => this.dialogRef.close()))
      .subscribe((resp) => {
        console.log(resp);
      });
  }
}
