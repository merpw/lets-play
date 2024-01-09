import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { OrderStatus } from 'src/app/shared/models/order';

@Component({
  selector: 'app-confirm-order-modify',
  templateUrl: './confirm-order-modify.component.html',
  styleUrls: ['./confirm-order-modify.component.scss'],
})
export class ConfirmOrderModifyComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmOrderModifyComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}

  onBack() {
    this.dialogRef.close(false);
  }

  onCancel() {
    console.log('cancelling');
    this.dialogRef.close(OrderStatus.CANCELLED);
  }

  onConfirm() {
    console.log('Confirmed');
    this.dialogRef.close(OrderStatus.CONFIRMED);
  }
}
