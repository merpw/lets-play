import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddProductModalComponent } from './add-product-modal/add-product-modal.component';
import { ProductService } from 'src/app/shared/services/product.service';
import { firstValueFrom, map } from 'rxjs';
import { UserService } from 'src/app/shared/services/user.service';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';
import { Product } from 'src/app/shared/models/product.model';

@Component({
  selector: 'app-product-listing-page',
  templateUrl: './product-listing-page.component.html',
  styleUrls: ['./product-listing-page.component.scss'],
})
export class ProductListingPageComponent implements OnInit {
  result: Result | null = null;

  public displayedColumns = [
    'images',
    'name',
    'description',
    'price',
    'quantity',
    'owner',
  ];

  public dataSource: Product[] = [];
  public isLoading = false;

  constructor(
    public dialog: MatDialog,
    public authService: AuthService,
    public userService: UserService,
    public screenSizeService: ScreenSizeService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.fetchProducts(false);
  }

  fetchProducts(clearCache?: boolean) {
    this.productService
      .getProducts(clearCache)
      .pipe(map((resp) => resp.body))
      .subscribe({
        next: async (products: Array<Product>) => {
          if (products.length === 0) {
            this.result = {
              type: 'info',
              message: 'The product list is currently empty.',
            };
            return;
          }
          const owners = await firstValueFrom(
            this.userService.getUsers(
              products.map((product: Product) => product.userId)
            )
          );
          products.map((product, i) => {
            product['owner'] = owners[i]?.name || 'NotFound';
            return product;
          });
          this.dataSource = products.slice().reverse();
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Sign up went wrong. Please try again.';
          this.result = { type: 'error', message: errorMessage };
        },
      });
  }

  openAddProductDialog(): void {
    this.result = null;

    const dialogRef = this.dialog.open(AddProductModalComponent);

    dialogRef.afterClosed().subscribe((result) => {
      this.result = result;
      this.fetchProducts(true);
    });
  }
}
