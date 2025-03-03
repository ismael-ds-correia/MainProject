import { TestBed } from '@angular/core/testing';

import { AppointmentTypeService } from './appointment-type.service';

describe('AppointmentTypeService', () => {
  let service: AppointmentTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AppointmentTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
