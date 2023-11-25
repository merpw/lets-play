import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddProductModalComponent } from './add-product-modal/add-product-modal.component';
import { HttpClient } from '@angular/common/http';
import { ProductService } from 'src/app/shared/product.service';
import { forkJoin, map, mergeMap, firstValueFrom } from 'rxjs';
import { UserService } from 'src/app/shared/user.service';

@Component({
  selector: 'app-product-listing-page',
  templateUrl: './product-listing-page.component.html',
  styleUrls: ['./product-listing-page.component.scss'],
})
export class ProductListingPageComponent implements OnInit {
  result: any = {};

  public displayedColumns = [
    'id',
    'image',
    'name',
    'description',
    'price',
    'quantity',
    'userId',
  ];

  public dataSource: any;
  public isLoading = false;

  constructor(
    public dialog: MatDialog,
    private productService: ProductService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    // fetch products here
    this.fetchProducts();
  }

  fetchProducts() {
    this.productService
      .getProducts()
      .pipe(
        map(async (resp) => {
          const usernames = await firstValueFrom(
            this.userService.getUsers(resp.body.map((data: any) => data.userId))
          );
          return resp.body;
        })
      )
      .subscribe({
        next: async (products) => {
          const data: Array<any> = await products;
          this.dataSource = data.reverse();
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
    this.result = {};

    const dialogRef = this.dialog.open(AddProductModalComponent);

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
      console.log(result);
      this.result = result;
      this.fetchProducts();
    });
  }
}
