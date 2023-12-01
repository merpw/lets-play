import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddProductModalComponent } from './add-product-modal/add-product-modal.component';
import { ProductService } from 'src/app/shared/services/product.service';
import { map } from 'rxjs';
import { UserService } from 'src/app/shared/services/user.service';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-product-listing-page',
  templateUrl: './product-listing-page.component.html',
  styleUrls: ['./product-listing-page.component.scss'],
})
export class ProductListingPageComponent implements OnInit {
  result: Result | null = null;

  public displayedColumns = [
    // 'id', // not showing product id to user
    'image',
    'name',
    'description',
    'price',
    'quantity',
    'owner',
  ];

  public dataSource: any;
  public isLoading = false;

  constructor(
    public dialog: MatDialog,
    public authService: AuthService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    // fetch products here
    this.fetchProducts(false);
  }

  fetchProducts(clearCache?: boolean) {
    this.productService
      .getProducts(clearCache)
      .pipe(
        map(async (resp) => {
          // FIXME: GET /api/users/{id} is returning 401
          // const usernames = await firstValueFrom(
          //   this.userService.getUsers(resp.body.map((data: any) => data.userId))
          // );
          return resp.body;
        })
      )
      .subscribe({
        next: async (products) => {
          const data: Array<any> = await products;
          this.dataSource = data.slice().reverse();
          if (data.length === 0) {
            this.result = {
              type: 'info',
              message: 'The product list is currently empty.',
            };
          }
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Sign up went wrong. Please try again.';
          console.log(errorMessage);
          this.result = { type: 'error', message: errorMessage };
        },
      });
  }

  openAddProductDialog(): void {
    this.result = null;

    const dialogRef = this.dialog.open(AddProductModalComponent);

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
      console.log(result);
      this.result = result;
      this.fetchProducts(true);
    });
  }
}
