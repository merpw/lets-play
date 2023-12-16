import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { map, of } from 'rxjs';
import { ProductDetailsComponent } from 'src/app/components/product-details/product-details.component';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';
import { AddProductModalComponent } from '../product-listing-page/add-product-modal/add-product-modal.component';
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
  public dataSource: Product[] = [];
  public result: Result | null = null;

  constructor(
    public dialog: MatDialog,
    public screenSizeService: ScreenSizeService,
    public authService: AuthService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.fetchProductsSellerManagement(localStorage.getItem('userId') || '');
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

  fetchProductsSellerManagement(userId: string, clearCache?: boolean) {
    let fetchProducts$;
    if (this.authService.profile?.role === 'admin') {
      fetchProducts$ = this.productService
        .getProducts(clearCache)
        .pipe(map((resp) => resp.body));
    } else if (this.authService.profile?.role === 'seller') {
      fetchProducts$ = this.productService.getProductsFilteredByUserId(
        userId,
        clearCache
      );
    } else {
      fetchProducts$ = of([]);
    }
    fetchProducts$.subscribe({
      next: (products) => {
        if (products.length === 0) {
          this.result = {
            type: 'info',
            message: 'The product list is currently empty.',
          };
          return;
        }
        this.dataSource = products.slice().reverse();
      },
      error: () => {
        this.result = {
          type: 'error',
          message: 'Error in fetching your products',
        };
      },
    });
  }

  openManageDialog(product: Product): void {
    const dialogRef = this.dialog.open(MediaManagementPageComponent, {
      data: { product },
    });

    dialogRef.afterClosed().subscribe((result) => {
      this.result = result;
      this.fetchProductsSellerManagement(
        localStorage.getItem('userId') || '',
        true
      );
    });
  }

  openAddProductDialog(): void {
    this.result = null;

    const dialogRef = this.dialog.open(AddProductModalComponent);

    dialogRef.afterClosed().subscribe((result) => {
      this.result = result;
      this.fetchProductsSellerManagement(
        localStorage.getItem('userId') || '',
        true
      );
    });
  }
}
