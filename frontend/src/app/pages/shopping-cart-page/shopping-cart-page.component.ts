import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { finalize, Subject, takeUntil } from 'rxjs';
import { ConfirmComponent } from 'src/app/components/confirm/confirm.component';
import { Result } from 'src/app/shared/models/result.model';
import { ShoppingCartItem } from 'src/app/shared/models/shopping-cart-item.model';
import { ShoppingCart } from 'src/app/shared/models/shopping-cart.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';
import { UserService } from 'src/app/shared/services/user.service';
import { ConfirmOrderComponent } from './confirm-order/confirm-order.component';

@Component({
  selector: 'app-shopping-cart-page',
  templateUrl: './shopping-cart-page.component.html',
  styleUrls: ['./shopping-cart-page.component.scss'],
})
export class ShoppingCartPageComponent implements OnInit, OnDestroy {
  result: Result | null = null;

  public displayedColumns = ['image', 'name', 'quantity', 'price', 'delete'];

  public dataSource: ShoppingCartItem[] = [];
  public isLoading = false;
  public fetchingShoppingCart = true;
  public form: FormGroup = new FormGroup({});

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
    this.fetchShoppingCart();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchShoppingCart() {
    this.fetchingShoppingCart = true;
    this.result = null;

    const shoppingCart: ShoppingCart | null =
      this.loadShoppingCartFromStorage();
    if (!shoppingCart || shoppingCart.items.length === 0) {
      this.dataSource = [];
      this.showShoppingCartEmptyMessage();
      this.fetchingShoppingCart = false;
      return;
    }
    this.form = new FormGroup({});
    shoppingCart.items.forEach((item) => {
      const control = new FormControl(
        item.quantity,
        Validators.max(item.product.quantity)
      );
      this.form.addControl(item.product.id, control);
      control.valueChanges
        .pipe(takeUntil(this.destroy$))
        .subscribe((quantity) => {
          this.updateShoppingCartById(item.product.id, quantity);
        });
    });

    this.dataSource = shoppingCart.items;
    this.fetchingShoppingCart = false;
  }

  private updateShoppingCartById(productId: string, quantity: number | null) {
    if (!quantity || !productId) return;
    const shoppingCart = this.loadShoppingCartFromStorage();
    if (!shoppingCart) return;
    shoppingCart.items = shoppingCart.items.map((item) => {
      if (item.product.id === productId) {
        item.quantity = quantity;
      }
      return item;
    });
    localStorage.setItem(
      <string>this.productService.shoppingCartKey,
      JSON.stringify(shoppingCart)
    );
  }

  onDeleteItem(itemToDelete: ShoppingCartItem) {
    const dialogRef = this.dialog.open(ConfirmComponent, {
      data: {
        message: `Do you want to delete ${itemToDelete.product.name} x ${itemToDelete.quantity}?`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      if (this.productService.shoppingCartKey) {
        this.deleteItemFromStorage(itemToDelete);
        this.fetchShoppingCart();
      }
    });
  }

  deleteItemFromStorage(itemToDelete: ShoppingCartItem) {
    const shoppingCart: ShoppingCart | null =
      this.loadShoppingCartFromStorage();
    if (!shoppingCart) return;

    shoppingCart.items = shoppingCart.items.filter(
      (item) => item.product.id !== itemToDelete.product.id
    );
    localStorage.setItem(
      <string>this.productService.shoppingCartKey,
      JSON.stringify(shoppingCart)
    );

    this.fetchShoppingCart();
  }

  submitOrder() {
    if (!this.loadShoppingCartFromStorage()) {
      this.showOrderFailMessage();
      this.isLoading = false;
      return;
    }

    const shoppingCart = this.loadShoppingCartFromStorage();

    const dialogRef = this.dialog.open(ConfirmOrderComponent, {
      data: shoppingCart,
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result || !shoppingCart) return;
      this.isLoading = true;
      this.orderService
        .createOrder(shoppingCart)
        .pipe(finalize(() => (this.isLoading = false)))
        .subscribe({
          next: (resp) => {
            this.showOrderSuccessMessage();
            if (this.productService.shoppingCartKey) {
              localStorage.removeItem(this.productService.shoppingCartKey);
            }
            this.dataSource = [];
          },
          error: (error) => {
            this.showOrderFailMessage();
          },
        });
    });
  }

  private loadShoppingCartFromStorage(): ShoppingCart | null {
    const shoppingCartKey = this.productService.shoppingCartKey;
    try {
      if (!shoppingCartKey) {
        this.showShoppingCartErrorMessage();
        return null;
      }
      const shoppingCart: ShoppingCart = JSON.parse(
        <string>localStorage.getItem(shoppingCartKey)
      );
      return shoppingCart;
    } catch {
      this.showShoppingCartErrorMessage();
      if (shoppingCartKey) {
        localStorage.removeItem(shoppingCartKey);
      }
      return null;
    }
  }

  private showShoppingCartErrorMessage() {
    this.result = {
      type: 'error',
      message: 'Your shopping cart contains an error.',
    };
  }

  private showShoppingCartEmptyMessage() {
    this.result = {
      type: 'info',
      message: 'Your shopping cart is empty.',
    };
  }

  private showOrderSuccessMessage() {
    this.result = {
      type: 'success',
      message: 'Your order is submitted successfully.',
    };
  }

  private showOrderFailMessage() {
    this.result = {
      type: 'error',
      message: 'Error happen in order submission Please try again.',
    };
  }
}
