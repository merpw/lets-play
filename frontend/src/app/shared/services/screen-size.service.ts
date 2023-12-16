import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Injectable, OnDestroy } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ScreenSizeService implements OnDestroy {
  private destroyed = new Subject<void>();
  public currentScreenSize = '';

  displayNameMap = new Map([
    [Breakpoints.XSmall, 'XS'],
    [Breakpoints.Small, 'S'],
    [Breakpoints.Medium, 'M'],
    [Breakpoints.Large, 'L'],
    [Breakpoints.XLarge, 'XL'],
  ]);

  constructor(private breakpointObserver: BreakpointObserver) {
    breakpointObserver
      .observe([
        Breakpoints.XSmall,
        Breakpoints.Small,
        Breakpoints.Medium,
        Breakpoints.Large,
        Breakpoints.XLarge,
      ])
      .pipe(takeUntil(this.destroyed))
      .subscribe((result) => {
        for (const query of Object.keys(result.breakpoints)) {
          if (result.breakpoints[query]) {
            this.currentScreenSize = this.displayNameMap.get(query) || 'XS';
          }
        }
      });
  }

  ngOnDestroy(): void {
    this.destroyed.next();
    this.destroyed.complete();
  }
}
