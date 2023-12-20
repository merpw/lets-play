import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
  Subject,
  finalize,
  catchError,
  EMPTY,
  map,
  mergeMap,
  forkJoin,
} from 'rxjs';
import { Order } from 'src/app/shared/models/order';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';
import { UserService } from 'src/app/shared/services/user.service';
import { ConfirmOrderModifyComponent } from '../confirm-order-modify/confirm-order-modify.component';
import { ConfirmComponent } from '../confirm/confirm.component';

@Component({
  selector: 'app-order-history',
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.scss'],
})
export class OrderHistoryComponent implements OnInit, OnDestroy {
  result: Result | null = null;

  public displayedColumns = [
    'image',
    'name',
    'quantity',
    'price',
    'status',
    'action',
  ];

  public dataSource: { order: Order; product: Product }[] = [];
  public isLoading = false;
  public fetchingOrderHistory = true;

  private destroy$ = new Subject<void>();

  constructor(
    public dialog: MatDialog,
    public authService: AuthService,
    public userService: UserService,
    public screenSizeService: ScreenSizeService,
    private productService: ProductService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.fetchOrderHistory();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchOrderHistory() {
    this.fetchingOrderHistory = true;
    this.result = null;

    this.orderService
      .getOrderHistory()
      .pipe(
        mergeMap((orders: Order[]) =>
          forkJoin(
            orders.map((order) =>
              this.productService.getProductById(order.productId)
            )
          ).pipe(
            map((products: Product[]) =>
              products.map((product, i) => {
                return { product: product, order: orders[i] };
              })
            )
          )
        ),
        finalize(() => (this.fetchingOrderHistory = false)),
        catchError(() => {
          this.showOrderHistoryErrorMessage();
          return EMPTY;
        })
      )
      .subscribe((orders) => {
        if (!orders || orders.length === 0) {
          this.showOrderHistoryEmptyMessage();
        }
        this.dataSource = orders;
      });
  }

  deleteOrder(order: Order) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: {
        message: `Do you want to delete order?`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.orderService
        .deleteOrder(order.id)
        .pipe(
          catchError(() => {
            this.showOrderFailMessage();
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.fetchOrderHistory();
          this.showOrderSuccessMessage('Your order is deleted successfully');
        });
    });
  }

  updateOrder(order: Order) {
    const dialogRef = this.dialog.open(ConfirmOrderModifyComponent, {
      data: {
        message: `Do you want to confirm or cancel the order?`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;

      this.orderService
        .updateOrderStatus(order.id, result)
        .pipe(
          catchError(() => {
            this.showOrderFailMessage();
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.fetchOrderHistory();
          this.showOrderSuccessMessage(`Your order is ${result} successfully`);
        });
    });
  }

  onAction(order: Order) {
    if (this.authService.profile?.role === 'user') {
      this.deleteOrder(order);
    } else {
      this.updateOrder(order);
    }
  }

  private showOrderHistoryErrorMessage() {
    this.result = {
      type: 'error',
      message: 'Your order history contains an error.',
    };
  }

  private showOrderHistoryEmptyMessage() {
    this.result = {
      type: 'info',
      message: 'Your order history is empty.',
    };
  }

  private showOrderSuccessMessage(message: string) {
    this.result = {
      type: 'success',
      message: message,
    };
  }

  private showOrderFailMessage() {
    this.result = {
      type: 'error',
      message: 'Error happened. Please try again.',
    };
  }
}
