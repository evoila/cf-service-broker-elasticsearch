import { TestBed, inject } from '@angular/core/testing';

import { AutoScalerService } from './auto-scaler.service';

describe('AutoScalerService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AutoScalerService]
    });
  });

  it('should be created', inject([AutoScalerService], (service: AutoScalerService) => {
    expect(service).toBeTruthy();
  }));
});
