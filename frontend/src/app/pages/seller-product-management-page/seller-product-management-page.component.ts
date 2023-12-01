import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { MediaManagementPageComponent } from './media-management-page/media-management-page.component';

@Component({
  selector: 'app-seller-product-management-page',
  templateUrl: './seller-product-management-page.component.html',
  styleUrls: ['./seller-product-management-page.component.scss'],
})
export class SellerProductManagementPageComponent implements OnInit {
  public displayedColumns = [
    // 'id',
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
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.fetchProductsFilteredByUserId(localStorage.getItem('userId') || '');
  }

  fetchProductsFilteredByUserId(userId: string, clearCache?: boolean) {
    this.productService
      .getProductsFilteredByUserId(userId, clearCache)
      .subscribe({
        next: (products) => {
          this.dataSource = products;
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
