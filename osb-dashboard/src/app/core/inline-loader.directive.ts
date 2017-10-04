import { Directive, Input, ElementRef, Renderer } from '@angular/core';

export type LoaderColor = 'black' | 'white';
@Directive({
  selector: '[mstInlineLoader]'
})
export class InlineLoaderDirective {

  private child: any;

  @Input()
  public loaderColor: LoaderColor = 'white';

  constructor(private readonly el: ElementRef, private readonly renderer: Renderer) { }

  @Input()
  public set mstInlineLoader(value: boolean) {
    if (value) {
      this.child = this.child || this.createChild();
      this.renderer.invokeElementMethod(this.el.nativeElement, 'insertBefore', [this.child, this.el.nativeElement.firstChild]);
    } else if (this.child) {
      this.renderer.invokeElementMethod(this.el.nativeElement, 'removeChild', [this.child]);
    }
  }

  private createChild() {
    const c = this.renderer.createElement(null, 'span'); // need to use span as div is illegal, e.g. in buttons
    this.renderer.setElementClass(c, 'loader', true);
    this.renderer.setElementClass(c, 'loader-inline', true);

    if (this.loaderColor === 'white') {
      this.renderer.setElementClass(c, 'loader-white', true);
    }
    return c;
  }
}

