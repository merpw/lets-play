import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MediaManagementPageComponent } from './media-management-page.component';

describe('MediaManagementPageComponent', () => {
  let component: MediaManagementPageComponent;
  let fixture: ComponentFixture<MediaManagementPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MediaManagementPageComponent]
    });
    fixture = TestBed.createComponent(MediaManagementPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
