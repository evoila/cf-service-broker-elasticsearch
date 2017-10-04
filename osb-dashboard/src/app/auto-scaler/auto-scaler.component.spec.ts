import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AutoScalerComponent } from './auto-scaler.component';

describe('AutoScalerComponent', () => {
  let component: AutoScalerComponent;
  let fixture: ComponentFixture<AutoScalerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AutoScalerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AutoScalerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
