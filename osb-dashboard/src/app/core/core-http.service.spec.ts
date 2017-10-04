import { TestBed, inject } from '@angular/core/testing';

import { CoreHttpService } from './core-http.service';

describe('CoreHttpService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CoreHttpService]
    });
  });

  it('should ...', inject([CoreHttpService], (service: CoreHttpService) => {
    expect(service).toBeTruthy();
  }));
});
