import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ProductDetailsComponent } from 'src/app/components/product-details/product-details.component';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { ProductService } from 'src/app/shared/services/product.service';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';
import { MediaManagementPageComponent } from './media-management-page/media-management-page.component';

@Component({
  selector: 'app-seller-product-management-page',
  templateUrl: './seller-product-management-page.component.html',
  styleUrls: ['./seller-product-management-page.component.scss'],
})
export class SellerProductManagementPageComponent implements OnInit {
  public displayedColumns = [
    'image',
    'name',
    'description',
    'price',
    'quantity',
    'manage',
  ];
  public dataSource: any;
  public result: Result | null = null;

  constructor(
    public dialog: MatDialog,
    public screenSizeService: ScreenSizeService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.fetchProductsFilteredByUserId(localStorage.getItem('userId') || '');
  }

  openProductDetailsDialog(product: Product) {
    console.log(product);
    const dialogRef = this.dialog.open(ProductDetailsComponent, {
      data: { product },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
      console.log(result);
    });
  }

  fetchProductsFilteredByUserId(userId: string, clearCache?: boolean) {
    this.productService
      .getProductsFilteredByUserId(userId, clearCache)
      .subscribe({
        next: (products) => {
          this.dataSource = products.slice().reverse();
          if (products.length === 0) {
            this.result = {
              type: 'info',
              message: 'The product list is currently empty.',
            };
          }
        },
        error: (err) => {
          console.log(err);
          this.result = {
            type: 'error',
            message: 'Error in fetching your products',
          };
        },
      });
  }

  openManageDialog(product: any): void {
    console.log(product);
    const dialogRef = this.dialog.open(MediaManagementPageComponent, {
      data: { product },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
      console.log(result);
      this.result = result;
      this.fetchProductsFilteredByUserId(
        localStorage.getItem('userId') || '',
        true
      );
    });
  }
}
