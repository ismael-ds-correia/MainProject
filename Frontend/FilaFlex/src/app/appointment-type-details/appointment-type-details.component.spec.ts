import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentTypeDetailsComponent } from './appointment-type-details.component';

describe('AppointmentTypeDetailsComponent', () => {
  let component: AppointmentTypeDetailsComponent;
  let fixture: ComponentFixture<AppointmentTypeDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentTypeDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentTypeDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
