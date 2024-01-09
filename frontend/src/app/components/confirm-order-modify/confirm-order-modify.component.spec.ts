import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmOrderModifyComponent } from './confirm-order-modify.component';

describe('ConfirmOrderModifyComponent', () => {
  let component: ConfirmOrderModifyComponent;
  let fixture: ComponentFixture<ConfirmOrderModifyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmOrderModifyComponent],
    });
    fixture = TestBed.createComponent(ConfirmOrderModifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
