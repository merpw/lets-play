import { Observable } from 'rxjs';
import { Product } from './product.model';

export enum OrderStatus {
  PENDING = 'pending',
  CONFIRMED = 'confirmed',
  COMPLETED = 'completed',
  CANCELLED = 'canceled',
}

export interface Order {
  id: string;
  products: {
    id: string;
    quantity: number;
    info: Product;
  }[];
  buyer: string;
  seller: string;
  totalPrice: number;
  status: OrderStatus;
  createdAt: Date;
  orderHistoryName: string;
  buyerName: Observable<string>;
  sellerName: Observable<string>;
}
