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
  of,
  tap,
} from 'rxjs';
import { Order, OrderStatus } from 'src/app/shared/models/order';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';
import { UserService } from 'src/app/shared/services/user.service';
import { ConfirmComponent } from '../confirm/confirm.component';

@Component({
  selector: 'app-order-history',
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.scss'],
})
export class OrderHistoryComponent implements OnInit, OnDestroy {
  result: Result | null = null;

  public displayedColumns = ['image', 'name', 'quantity'];

  public orders: Order[] = [];
  public dataSource: Order[] = [];
  public isLoading = false;
  public fetchingOrderHistory = true;
  public role: string | undefined = '';

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
    this.role = this.authService.profile?.role;
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
        map((resp) => resp.body),
        tap((orders) => (this.orders = orders.reverse())),
        mergeMap((orders: Order[]) => {
          if (!orders.length) return of([]);
          return forkJoin(
            orders.map((order: Order) =>
              forkJoin(
                order.products.map((product) =>
                  this.productService.getProductById(product.id).pipe(
                    map((productInfo: Product) => {
                      product.info = productInfo;
                      return order;
                    })
                  )
                )
              )
            )
          ).pipe(
            map((orders) => orders.flat()),
            map((orders) =>
              orders.map((order) => {
                order.buyerName = this.userService.getUserNameById(order.buyer);
                order.sellerName = this.userService.getUserNameById(
                  order.seller
                );
                return order;
              })
            )
          );
        }),
        finalize(() => (this.fetchingOrderHistory = false)),
        catchError(() => {
          this.showOrderHistoryErrorMessage();
          return EMPTY;
        })
      )
      .subscribe((orders: Order[]) => {
        if (!orders || orders.length === 0) {
          this.showOrderHistoryEmptyMessage();
        }
        this.dataSource = orders;
      });
  }

  onDelete(id: string) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: {
        message: `Do you want to delete the order?`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result || this.role !== 'admin') return;
      this.orderService.deleteOrder(id).subscribe({
        next: () => {
          this.fetchOrderHistory();
          this.showOrderSuccessMessage(
            `Order ID ${id} is deleted successfully.`
          );
        },
        error: () => {
          this.fetchOrderHistory();
          this.showOrderFailMessage();
        },
      });
    });
  }

  onAction(action: string, id: string) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: {
        message: `Do you want to ${action} the order?`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      let status: OrderStatus;
      switch (action) {
        case 'accept':
          status = OrderStatus.CONFIRMED;
          break;
        case 'cancel':
          status = OrderStatus.CANCELLED;
          break;
        case 'complete':
          status = OrderStatus.COMPLETED;
          break;
        default:
          status = OrderStatus.PENDING;
      }
      this.orderService
        .updateOrderStatus(id, status)
        .pipe(
          catchError(() => {
            this.showOrderFailMessage();
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.fetchOrderHistory();
          this.showOrderSuccessMessage(
            `Order ID ${id} is now ${status} successfully`
          );
        });
    });
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
