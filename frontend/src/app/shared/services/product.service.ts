import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, of, tap } from 'rxjs';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private productsApiBaseURL = 'api/products';
  private addProductsUrl = this.productsApiBaseURL + '/add';

  private products: any = undefined;

  constructor(private http: HttpClient) {}

  getProducts(clearCache?: boolean): Observable<any> {
    if (this.products === undefined || clearCache) {
      return this.http
        .get(this.productsApiBaseURL, { observe: 'response' })
        .pipe(tap((resp) => (this.products = resp.body)));
    }
    return of({ body: this.products });
  }

  getProductsFilteredByUserId(
    userId: string,
    clearCache?: boolean
  ): Observable<any> {
    return this.getProducts(clearCache).pipe(
      map((resp) => {
        return resp.body.filter((product: any) => product.userId === userId);
      })
    );
  }

  addProducts(product: Product): Observable<HttpResponse<string>> {
    return this.http.post(this.addProductsUrl, product, {
      withCredentials: true,
      observe: 'response',
      responseType: 'text',
    });
  }

  deleteProduct(productId: string): Observable<any> {
    return this.http.delete(this.productsApiBaseURL + '/' + productId, {
      withCredentials: true,
      observe: 'response',
    });
  }

  updateProduct(productId: string, productsDetails: any): Observable<any> {
    return this.http.put(
      this.productsApiBaseURL + '/' + productId,
      productsDetails,
      {
        withCredentials: true,
        observe: 'response',
      }
    );
  }

  getProductById(productId: string): Observable<any> {
    return this.http.get(this.productsApiBaseURL + '/' + productId);
  }
}
