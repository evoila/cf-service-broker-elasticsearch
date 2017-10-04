import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'sb-no-content',
  template: `
    <div class="form-modal">
      <h1>Not Found</h1>
      <p>It appears we can't find the page you requested.</p>
      <p> That's an error on our part and we're sorry! Our team has been pinged about this issue and we will work to fix it.</p>
      <a class="nav-link" align="center" routerLink="/">Home</a>
    </div>
  `
})
export class NoContentComponent implements OnInit {

  constructor() {}

  ngOnInit() {}
}
