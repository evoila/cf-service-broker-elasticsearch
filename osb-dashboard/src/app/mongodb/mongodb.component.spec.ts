import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MongoDbComponent } from './mongodb.component';

describe('MongodbComponent', () => {
  let component: MongoDbComponent;
  let fixture: ComponentFixture<MongoDbComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MongoDbComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MongoDbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
