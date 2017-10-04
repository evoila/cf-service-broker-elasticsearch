import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Notification } from 'app/core';

@Component({
  selector: 'sb-notification-banner',
  templateUrl: './notification-banner.component.html',
  styleUrls: ['./notification-banner.component.scss']
})
export class NotificationBannerComponent implements OnInit {
  @Input()
  public notification: Notification;

  @Output()
  public readonly close: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() { }

  public onClose() {
    this.close.emit();
  }
}
