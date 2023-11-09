import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MediaManagementPageComponent } from './media-management-page/media-management-page.component';

@Component({
  selector: 'app-seller-product-management-page',
  templateUrl: './seller-product-management-page.component.html',
  styleUrls: ['./seller-product-management-page.component.scss'],
})
export class SellerProductManagementPageComponent implements OnInit {
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
    'manage',
  ];
  public dataSource: any;

  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {
    // fetch products here
    this.dataSource = this.data;
  }

  openManageDialog(product: any): void {
    const dialogRef = this.dialog.open(MediaManagementPageComponent, {
      data: { product },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('The dialog was closed');
      console.log(result);
    });
  }
}
