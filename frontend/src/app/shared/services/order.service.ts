import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, map, mergeMap, Observable } from 'rxjs';
import { OrderStatus } from '../models/order';
import { Product } from '../models/product.model';
import { ShoppingCartItem } from '../models/shopping-cart-item.model';
import { ShoppingCart } from '../models/shopping-cart.model';
import { ProductService } from './product.service';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private orderApiBaseUrl = 'api/orders';

  constructor(
    private http: HttpClient,
    private productService: ProductService
  ) {}

  getOrderTotalPrice(shoppingCartItem: ShoppingCartItem[]): Observable<number> {
    return forkJoin(
      shoppingCartItem.map((item) =>
        this.productService
          .getProductById(item.product.id)
          .pipe(map((product: Product) => product.price * item.quantity))
      )
    ).pipe(map((amounts) => amounts.reduce((sum, amount) => amount + sum), 0));
  }

  createOrder(shoppingCart: ShoppingCart): Observable<any> {
    const ordersList: {
      [sellerId: string]: ShoppingCartItem[];
    } = {};
    shoppingCart.items.forEach((item) => {
      if (ordersList[item.product.userId]) {
        ordersList[item.product.userId].push(item);
      } else {
        ordersList[item.product.userId] = [item];
      }
    });

    return forkJoin(
      Object.entries(ordersList).map(([sellerId, shoppingCartItems]) => {
        return this.getOrderTotalPrice(shoppingCartItems).pipe(
          mergeMap((totalPrice) => {
            console.log(totalPrice);
            const payload = {
              products: shoppingCartItems.map((item) => {
                return {
                  id: item.product.id,
                  quantity: item.quantity,
                };
              }),
              totalPrice: totalPrice,
              seller: sellerId,
            };
            return this.http.post(this.orderApiBaseUrl + '/add', payload, {
              withCredentials: true,
              responseType: 'text',
              observe: 'response',
            });
          })
        );
      })
    );
  }

  // TODO:
  updateOrderStatus(orderId: string, status: OrderStatus): Observable<any> {
    return this.http.put(
      this.orderApiBaseUrl + '/' + orderId,
      { status: status },
      {
        withCredentials: true,
        responseType: 'json',
        observe: 'response',
      }
    );
  }

  // TODO:
  deleteOrder(orderId: string): Observable<any> {
    return this.http.delete(this.orderApiBaseUrl + '/' + orderId, {
      withCredentials: true,
      responseType: 'json',
      observe: 'response',
    });
  }

  getOrderHistory(): Observable<any> {
    return this.http.get(this.orderApiBaseUrl, {
      withCredentials: true,
      responseType: 'json',
      observe: 'response',
    });
  }
}
