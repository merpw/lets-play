import { Component, OnInit } from '@angular/core';
import { catchError, EMPTY, Subject } from 'rxjs';
import { Order } from 'src/app/shared/models/order';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';

@Component({
  selector: 'app-seller-profile',
  templateUrl: './seller-profile.component.html',
  styleUrls: ['./seller-profile.component.scss', '../home-page.component.scss'],
})
export class SellerProfileComponent implements OnInit {
  public mostSoldProductSubject$ = new Subject<{
    product: Product;
    quantity: number;
  }>();
  public mostRevenueProductSubject$ = new Subject<{
    product: Product;
    revenue: number;
  }>();

  public totalRevenueSubject$ = new Subject<number>();

  public result: Result | null = null;

  private sellingHistory: Order[] = [];

  constructor(
    public productService: ProductService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.orderService
      .getSellingHistory()
      .pipe(
        catchError(() => {
          this.result = {
            type: 'error',
            message: 'Error in fetching your order history.',
          };
          return EMPTY;
        })
      )
      .subscribe((resp) => {
        console.log(resp);
        this.sellingHistory = resp;
        if (this.sellingHistory.length === 0) {
          this.result = {
            type: 'info',
            message: 'Your order history is currently empty.',
          };
        }
        this.getMostSoldProduct();
        this.getMostRevenueProduct();
      });
  }

  getTotalRevenue() {
    const totalRevenue = this.sellingHistory.reduce(
      (total, order) => (total += order.totalPrice),
      0
    );
    this.totalRevenueSubject$.next(totalRevenue);
  }

  getMostSoldProduct() {
    const orderCount: { [productId: string]: number } = {};
    this.sellingHistory.forEach((order) => {
      if (orderCount[order.productId]) {
        orderCount[order.productId] += order.quantity;
      } else {
        orderCount[order.productId] = order.quantity;
      }
    });
    let mostCount = 0;
    let mostSoldProductId = '';
    for (const [productId, counter] of Object.entries(orderCount)) {
      if (counter > mostCount) {
        mostCount = counter;
        mostSoldProductId = productId;
      }
    }
    if (mostSoldProductId) {
      this.productService
        .getProductById(mostSoldProductId)
        .subscribe((product: Product) =>
          this.mostSoldProductSubject$.next({
            product: product,
            quantity: mostCount,
          })
        );
    }
  }

  getMostRevenueProduct() {
    const orderMoneySpent: { [productId: string]: number } = {};
    this.sellingHistory.forEach((order) => {
      if (orderMoneySpent[order.productId]) {
        orderMoneySpent[order.productId] += order.totalPrice;
      } else {
        orderMoneySpent[order.productId] = order.totalPrice;
      }
    });

    let mostSpent = 0;
    let mostSpentProductId = '';
    for (const [productId, spent] of Object.entries(orderMoneySpent)) {
      if (spent > mostSpent) {
        mostSpent = spent;
        mostSpentProductId = productId;
      }
    }
    if (mostSpentProductId) {
      this.productService
        .getProductById(mostSpentProductId)
        .subscribe((product: Product) => {
          this.mostRevenueProductSubject$.next({
            product: product,
            revenue: mostSpent,
          });
          this.getTotalRevenue();
        });
    }
  }
}
