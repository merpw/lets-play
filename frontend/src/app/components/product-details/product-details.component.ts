import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { finalize, firstValueFrom, tap } from 'rxjs';
import { Product } from 'src/app/shared/models/product.model';
import { Result } from 'src/app/shared/models/result.model';
import { ShoppingCartItem } from 'src/app/shared/models/shopping-cart-item.model';
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

  public form: FormGroup = new FormGroup({
    quantity: new FormControl(0),
  });

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
          if (this.product.quantity === 0) {
            this.handleUnavailableProduct();
          } else {
            this.form.controls['quantity'].addValidators(
              Validators.max(this.product.quantity)
            );
          }
        },
        error: () => {
          this.result = {
            message: 'Product is not found.',
            type: 'error',
          };
        },
      });
  }

  handleUnavailableProduct(): void {
    this.form.controls['quantity'].disable();
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

  addToCart(product: Product) {
    if (this.form.invalid) return;
    const item: ShoppingCartItem = {
      product,
      quantity: this.form.controls['quantity'].value,
    };
    if (this.productService.addProductToCart(item)) {
      this.result = {
        message: `${item.product.name} x ${item.quantity} is added to your shopping cart.`,
        type: 'success',
      };
      this.form.controls['quantity'].reset();
    } else {
      this.result = {
        message:
          'Your shopping cart contains an error. Your shopping cart is now empty. Please try again.',
        type: 'error',
      };
    }
  }
}
