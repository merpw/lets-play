import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, map, Observable, of } from 'rxjs';
import { Order, OrderStatus } from '../models/order';
import { Product } from '../models/product.model';
import { ShoppingCart } from '../models/shopping-cart.model';
import { AuthService } from './auth.service';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private orderApiBaseUrl = '';

  private mockOrderHistory: Order[] = [
    {
      id: 'string',
      productId: '6580a2f40b516b610252dcad',
      userId: 'string',
      sellerId: 'string',
      quantity: 1,
      totalPrice: 1,
      orderStatus: OrderStatus.CONFIRMED,
      timeStamp: 'string',
    },
    {
      id: 'string',
      productId: '6580a2f40b516b610252dcad',
      userId: 'string',
      sellerId: 'string',
      quantity: 1,
      totalPrice: 1,
      orderStatus: OrderStatus.CONFIRMED,
      timeStamp: 'string',
    },
    {
      id: 'string',
      productId: '658086190b516b610252dcac',
      userId: 'string',
      sellerId: 'string',
      quantity: 4,
      totalPrice: 60,
      orderStatus: OrderStatus.CONFIRMED,
      timeStamp: 'string',
    },
    {
      id: 'string',
      productId: '65834f365454055ac6d4ba65',
      userId: 'string',
      sellerId: 'string',
      quantity: 4,
      totalPrice: 60,
      orderStatus: OrderStatus.CONFIRMED,
      timeStamp: 'string',
    },
  ];

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private productService: ProductService
  ) {}

  getOrderTotalPrice(shoppingCart: ShoppingCart): Observable<number> {
    return forkJoin(
      shoppingCart.items.map((item) =>
        this.productService
          .getProductById(item.product.id)
          .pipe(map((product: Product) => product.price * item.quantity))
      )
    ).pipe(map((amounts) => amounts.reduce((sum, amount) => amount + sum), 0));
  }

  createOrder(shoppingCart: ShoppingCart): Observable<any> {
    return this.getOrderTotalPrice(shoppingCart).pipe(
      map((totalPrice) => {
        console.log(totalPrice);
        return this.http.post(this.orderApiBaseUrl, shoppingCart, {
          withCredentials: true,
          responseType: 'json',
          observe: 'response',
        });
      })
    );
  }

  updateOrder(shoppingCart: ShoppingCart): Observable<any> {
    return this.http.put(this.orderApiBaseUrl, shoppingCart, {
      withCredentials: true,
      responseType: 'json',
      observe: 'response',
    });
  }

  updateOrderStatus(orderId: string, status: OrderStatus): Observable<any> {
    return of(false);
    return this.http.put(
      this.orderApiBaseUrl,
      { orderId, status },
      {
        withCredentials: true,
        responseType: 'json',
        observe: 'response',
      }
    );
  }

  deleteOrder(orderId: string): Observable<any> {
    return this.http.put(this.orderApiBaseUrl, orderId, {
      withCredentials: true,
      responseType: 'json',
      observe: 'response',
    });
  }

  getOrderHistory(): Observable<any> {
    return of(this.mockOrderHistory);
    return this.http.get(this.orderApiBaseUrl, {
      withCredentials: true,
      responseType: 'json',
      observe: 'response',
    });
  }

  getSellingHistory(): Observable<any> {
    return of(this.mockOrderHistory);
    return this.http.get(this.orderApiBaseUrl, {
      withCredentials: true,
      responseType: 'json',
      observe: 'response',
    });
  }
}
