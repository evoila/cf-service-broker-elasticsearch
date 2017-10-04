import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestoreJobComponent } from './restore-job.component';

describe('RestoreJobComponent', () => {
  let component: RestoreJobComponent;
  let fixture: ComponentFixture<RestoreJobComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestoreJobComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreJobComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
