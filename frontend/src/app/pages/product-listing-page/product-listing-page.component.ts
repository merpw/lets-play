import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddProductModalComponent } from './add-product-modal/add-product-modal.component';

@Component({
  selector: 'app-product-listing-page',
  templateUrl: './product-listing-page.component.html',
  styleUrls: ['./product-listing-page.component.scss'],
})
export class ProductListingPageComponent implements OnInit {
  private data = [
    {
      id: 1,
      name: 'Apple',
      description: 'It is an apple',
      price: 10,
      quantity: 25,
      userId: 2,
      image: 'Image 1',
    },
    {
      id: 3,
      name: 'Orange',
      description: 'It is an orange',
      price: 5,
      quantity: 100,
      userId: 5,
      image: 'Image 2',
    },
    {
      id: 3,
      name: 'Lemon',
      description: 'It is an lemon',
      price: 20,
      quantity: 30,
      userId: 2,
      image: 'Image 3',
    },
  ];

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

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    // fetch products here
    this.dataSource = this.data;
  }

  openAddProductDialog(): void {
    const dialogRef = this.dialog.open(AddProductModalComponent);

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
      console.log(result);
    });
  }
}
