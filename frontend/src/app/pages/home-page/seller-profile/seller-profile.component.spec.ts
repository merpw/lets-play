import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellerProfileComponent } from './seller-profile.component';

describe('SellerProfileComponent', () => {
  let component: SellerProfileComponent;
  let fixture: ComponentFixture<SellerProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SellerProfileComponent],
      imports: [HttpClientTestingModule],
    });
    fixture = TestBed.createComponent(SellerProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
