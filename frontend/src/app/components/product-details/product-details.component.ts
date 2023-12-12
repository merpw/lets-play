import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { firstValueFrom, lastValueFrom } from 'rxjs';
import { AddProductModalComponent } from 'src/app/pages/product-listing-page/add-product-modal/add-product-modal.component';
import { Product } from 'src/app/shared/models/product.model';
import { UserService } from 'src/app/shared/services/user.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss'],
})
export class ProductDetailsComponent implements OnInit {
  public product!: Product;
  public imageIndex = 0;

  constructor(
    private userService: UserService,
    public dialogRef: MatDialogRef<ProductDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  async ngOnInit() {
    this.product = this.data.product;
    this.product.owner = await lastValueFrom(
      this.userService.getUserNameById(this.product.userId)
    );
  }

  nextImage() {
    if (this.product.images.length <= 1) return;
    if (this.imageIndex === this.product.images.length - 1) {
      this.imageIndex = 0;
    } else {
      this.imageIndex++;
    }
  }
}
