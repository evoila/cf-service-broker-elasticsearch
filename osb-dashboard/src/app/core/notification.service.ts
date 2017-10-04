import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

export type NotificationType = 'Warning';

export interface NotificationActionLink {
  title: string;
  routerLink: string[];
}

export class Notification {
  constructor(
    public readonly type: NotificationType,
    public readonly message: string,
    public readonly link?: NotificationActionLink) {
  }
}

@Injectable()
export class NotificationService {
  private readonly _notifications = new Subject<Notification>();

  public readonly notifications = this._notifications.asObservable();

  public add(notification: Notification) {
    this._notifications.next(notification);
  }
}
