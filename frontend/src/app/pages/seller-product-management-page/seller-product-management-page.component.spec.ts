import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellerProductManagementPageComponent } from './seller-product-management-page.component';

describe('SellerProductManagementPageComponent', () => {
  let component: SellerProductManagementPageComponent;
  let fixture: ComponentFixture<SellerProductManagementPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SellerProductManagementPageComponent]
    });
    fixture = TestBed.createComponent(SellerProductManagementPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
