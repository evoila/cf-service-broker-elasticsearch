import { Component, OnInit } from '@angular/core';
import { AutoScalerService } from './auto-scaler.service';

@Component({
  selector: 'sb-auto-scaler',
  templateUrl: './auto-scaler.component.html',
  styleUrls: ['./auto-scaler.component.scss']
})
export class AutoScalerComponent implements OnInit {
  readonly ENTITY = 'bindings';
  bindings: any[] = [];

  constructor(readonly asService: AutoScalerService) { }

  ngOnInit() {
    this.asService
      .loadAll(this.ENTITY)
      .subscribe((bindings: any) => {
        this.bindings = bindings.bindings;
      });
  }

}
