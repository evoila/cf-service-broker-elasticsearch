import { TestBed, inject } from '@angular/core/testing';

import { BackupService } from './backup.service';

describe('BackupServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BackupService]
    });
  });

  it('should be created', inject([BackupService], (service: BackupService) => {
    expect(service).toBeTruthy();
  }));
});
