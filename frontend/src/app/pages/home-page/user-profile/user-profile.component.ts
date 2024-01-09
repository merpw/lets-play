import { Component, OnInit } from '@angular/core';
import { catchError, EMPTY, Subject } from 'rxjs';
import { Order } from 'src/app/shared/models/order';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss', '../home-page.component.scss'],
})
export class UserProfileComponent implements OnInit {
  public mostOrderedProductSubject$ = new Subject<{
    product: Product;
    count: number;
  }>();
  public mostMoneySpentProductSubject$ = new Subject<{
    product: Product;
    moneySpent: number;
  }>();

  public totalMoneySpentSubject$ = new Subject<number>();

  public result: Result | null = null;

  private orderHistory: Order[] = [];

  constructor(
    public productService: ProductService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.orderService
      .getOrderHistory()
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
        this.orderHistory = resp.body;
        if (this.orderHistory.length === 0) {
          this.result = {
            type: 'info',
            message: 'Your order history is currently empty.',
          };
        }
        this.getMostOrderedProduct();
        this.getMostMoneySpentProduct();
      });
  }

  getTotalMoneySpent() {
    const totalMoneySpent = this.orderHistory.reduce(
      (total, order) => (total += order.totalPrice),
      0
    );
    this.totalMoneySpentSubject$.next(totalMoneySpent);
  }

  getMostOrderedProduct() {
    const orderCount: { [productId: string]: number } = {};
    this.orderHistory.forEach((order) => {
      order.products.forEach((product) => {
        if (orderCount[product.id]) {
          orderCount[product.id] += 1;
        } else {
          orderCount[product.id] = 1;
        }
      });
    });
    let mostCount = 0;
    let mostOrderedProductId = '';
    for (const [productId, counter] of Object.entries(orderCount)) {
      if (counter > mostCount) {
        mostCount = counter;
        mostOrderedProductId = productId;
      }
    }
    if (mostOrderedProductId) {
      this.productService
        .getProductById(mostOrderedProductId)
        .subscribe((product: Product) =>
          this.mostOrderedProductSubject$.next({
            product: product,
            count: mostCount,
          })
        );
    }
  }

  getMostMoneySpentProduct() {
    const orderMoneySpent: { [productId: string]: number } = {};
    this.orderHistory.forEach((order) => {
      order.products.forEach((product) => {
        if (orderMoneySpent[product.id]) {
          orderMoneySpent[product.id] += order.totalPrice;
        } else {
          orderMoneySpent[product.id] = order.totalPrice;
        }
      });
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
          this.mostMoneySpentProductSubject$.next({
            product: product,
            moneySpent: mostSpent,
          });
          this.getTotalMoneySpent();
        });
    }
  }
}
