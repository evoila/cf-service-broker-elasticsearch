import { Component, OnInit } from '@angular/core';
import { GeneralService } from './general.service';


@Component({
  selector: 'sb-general',
  templateUrl: './general.component.html',
  styleUrls: ['./general.component.scss']
})
export class GeneralComponent implements OnInit {
  generalInfo: any = {};

  constructor(protected readonly generalService: GeneralService) { }

  ngOnInit() {
    this.generalService.loadAll()
      .subscribe((info: any) => {
      console.log(info);
        this.generalInfo = info;
      });
  }
}
