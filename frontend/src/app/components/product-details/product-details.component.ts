import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize, firstValueFrom, tap } from 'rxjs';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { ProductService } from 'src/app/shared/services/product.service';
import { UserService } from 'src/app/shared/services/user.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.scss'],
})
export class ProductDetailsComponent implements OnInit {
  public product!: Product;
  public imageIndex = 0;
  public result: Result | null = null;
  public productLoaded = false;
  public owner = 'Not found';

  private id: string | undefined;

  constructor(
    private userService: UserService,
    private route: ActivatedRoute,
    private productService: ProductService
  ) {}

  async ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id') || '';
    if (!this.id) {
      this.result = {
        message: 'Product id is not found.',
        type: 'error',
      };
      return;
    }

    this.productService
      .getProductById(this.id)
      .pipe(
        finalize(() => (this.productLoaded = true)),
        tap(
          async (product: Product) =>
            (this.owner = await firstValueFrom(
              this.userService.getUserNameById(product.userId)
            ))
        )
      )
      .subscribe({
        next: (product: Product) => {
          this.product = product;
        },
        error: () => {
          this.result = {
            message: 'Product is not found.',
            type: 'error',
          };
        },
      });
  }

  nextImage() {
    if (this.product.images.length <= 1) return;
    if (this.imageIndex === this.product.images.length - 1) {
      this.imageIndex = 0;
    } else {
      this.imageIndex++;
    }
  }

  previousImage() {
    if (this.product.images.length <= 1) return;
    if (this.imageIndex === 0) {
      this.imageIndex = this.product.images.length - 1;
    } else {
      this.imageIndex--;
    }
  }
}
