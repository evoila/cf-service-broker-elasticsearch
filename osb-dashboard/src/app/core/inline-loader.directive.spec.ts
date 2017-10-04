import { TestBed, ComponentFixture } from '@angular/core/testing';
import { InlineLoaderDirective } from './inline-loader.directive';
import { Component, DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

@Component({
  template: `
  <button [mstInlineLoader]="isLoading"><span>text</span></button>
  `
})
class TestComponent {
  public isLoading: boolean = false;
}

describe('Directive: InlineLoader', () => {
  let fixture: ComponentFixture<TestComponent>;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(() => {
    fixture =
      TestBed
        .configureTestingModule({
          declarations: [InlineLoaderDirective, TestComponent]
        })
        .createComponent(TestComponent);

    fixture.detectChanges(); // initial binding
    // all elements with an attached HighlightDirective
    de = fixture.debugElement.query(By.directive(InlineLoaderDirective));
    el = de.nativeElement;
  });

  it('when loading is false, should have no loader', () => {
    fixture.componentInstance.isLoading = false;
    fixture.detectChanges();

    expect(el.children.length).toBe(1);
  });

  it('when loading is true, should have a loader as the first child', () => {
    fixture.componentInstance.isLoading = true;
    fixture.detectChanges();

    expect(el.children.length).toBe(2);
    expect(el.lastChild!.textContent).toBe('text');
  });


  it('when toggling loading, should remove loader', () => {
    fixture.componentInstance.isLoading = true;
    fixture.detectChanges();

    fixture.componentInstance.isLoading = false;
    fixture.detectChanges();

    expect(el.children.length).toBe(1);
    expect(el.lastChild!.textContent).toBe('text'); // check we removed the right element
  });
});
