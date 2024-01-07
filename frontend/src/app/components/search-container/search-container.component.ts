import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Product } from 'src/app/shared/models/product.model';
import { ScreenSizeService } from 'src/app/shared/services/screen-size.service';

@Component({
  selector: 'app-search-container',
  templateUrl: './search-container.component.html',
  styleUrls: ['./search-container.component.scss'],
})
export class SearchContainerComponent implements OnInit, OnDestroy {
  @Input() products: Product[] = [];
  @Output() updateSearch = new EventEmitter<Product[]>();

  public form = new FormGroup({
    searchKeyword: new FormControl(null),
    minPrice: new FormControl(null),
    maxPrice: new FormControl(null),
    seller: new FormControl(null),
    onlyAvailable: new FormControl(false),
  });

  public sellers: string[] = [''];

  private destroy$ = new Subject<void>();

  constructor(public screenSizeService: ScreenSizeService) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit(): void {
    this.sellers.push(
      ...Array.from(new Set(this.products.map((product) => product.owner)))
    );
    this.form.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((searchwords) => {
        this.handleSearching(searchwords);
      });
  }

  resetSearchForm(): void {
    this.form.reset();
  }

  handleSearching(searchwords: {
    [key: string]: string | number | boolean | null;
  }) {
    let filteredProducts = this.products.slice();
    for (const [key, value] of Object.entries(searchwords)) {
      if (!value) continue;

      if (key === 'searchKeyword') {
        filteredProducts = filteredProducts.filter((product) => {
          const productName = product.name.toLowerCase();
          const productDescription = product.description.toLowerCase();
          const keyword = (value as string).trim().toLowerCase();
          return (
            productName.includes(keyword) ||
            productDescription.includes(keyword)
          );
        });
      }
      if (key === 'minPrice' && Number.isFinite(value)) {
        filteredProducts = filteredProducts.filter(
          (product) => product.price >= (value as number)
        );
      }

      if (key === 'maxPrice' && Number.isFinite(value)) {
        filteredProducts = filteredProducts.filter(
          (product) => product.price <= (value as number)
        );
      }

      if (key === 'seller') {
        filteredProducts = filteredProducts.filter((product) =>
          product.owner.includes(value as string)
        );
      }

      if (key === 'onlyAvailable' && value === true) {
        filteredProducts = filteredProducts.filter(
          (product) => product.quantity > 0
        );
      }
    }
    this.updateSearch.emit(filteredProducts);
  }
}
