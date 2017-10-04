import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceKeysDetailComponent } from './service-keys-detail.component';

describe('FileEndpointComponent', () => {
  let component: ServiceKeysDetailComponent;
  let fixture: ComponentFixture<ServiceKeysDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceKeysDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceKeysDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
