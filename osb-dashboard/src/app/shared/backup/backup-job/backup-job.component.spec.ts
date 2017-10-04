import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BackupJobComponent } from './backup-job.component';

describe('BackupJobComponent', () => {
  let component: BackupJobComponent;
  let fixture: ComponentFixture<BackupJobComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BackupJobComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BackupJobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
