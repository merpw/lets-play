import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddProductModalComponent } from './add-product-modal/add-product-modal.component';
import { HttpClient } from '@angular/common/http';
import { ProductService } from 'src/app/shared/product.service';

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
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    // fetch products here
    this.fetchProducts();
  }

  fetchProducts() {
    this.productService.getProducts().subscribe({
      next: (resp) => {
        const data: Array<any> = resp.body;
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
