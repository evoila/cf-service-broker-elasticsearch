import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BackupJobDetailsComponent } from './backup-job-details.component';

describe('BackupJobDetailsComponent', () => {
  let component: BackupJobDetailsComponent;
  let fixture: ComponentFixture<BackupJobDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BackupJobDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BackupJobDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
