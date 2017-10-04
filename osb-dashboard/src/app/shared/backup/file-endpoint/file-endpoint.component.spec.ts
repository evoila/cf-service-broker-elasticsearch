import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FileEndpointComponent } from './file-endpoint.component';

describe('FileEndpointComponent', () => {
  let component: FileEndpointComponent;
  let fixture: ComponentFixture<FileEndpointComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FileEndpointComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FileEndpointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
