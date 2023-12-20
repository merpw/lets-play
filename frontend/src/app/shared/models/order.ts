export enum OrderStatus {
  PENDING = 'Pending',
  CONFIRMED = 'Confirmed',
  CANCELLED = 'Cancelled',
}

export interface Order {
  id: string;
  productId: string;
  userId: string;
  sellerId: string;
  quantity: number;
  totalPrice: number;
  orderStatus: OrderStatus;
  timeStamp: string;
}
