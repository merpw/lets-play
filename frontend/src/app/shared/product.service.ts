import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private getProductsUrl = 'api/products';
  private addProductsUrl = 'api/products/add';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<HttpResponse<any>> {
    return this.http.get(this.getProductsUrl, { observe: 'response' });
  }

  addProducts(product: any): Observable<HttpResponse<any>> {
    return this.http.post(this.addProductsUrl, product, {
      withCredentials: true,
      observe: 'response',
      responseType: 'text',
    });
  }
}
