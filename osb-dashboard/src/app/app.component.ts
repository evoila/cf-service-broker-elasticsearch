import { Component } from '@angular/core';

import { environment } from 'environments/runtime-environment';

import { NotificationService, Notification } from './core/notification.service';

import {
  BuildTargetService, ModuleSupport
} from 'app/shared';

@Component({
  selector: 'sb-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public logoSrc = environment.ui.logoSrc;
  public readonly moduleSupport: ModuleSupport;
  public readonly dynamicModuleSupport: any;

  public notification: Notification | null = null;

  constructor(
    private readonly notifications: NotificationService,
    buildTarget: BuildTargetService
  ) {
    this.dynamicModuleSupport = buildTarget.dynamicModuleSupport;
    this.moduleSupport = buildTarget.moduleSupport;

    this.notifications.notifications.subscribe(x => {
      console.log(x)
      this.notification = x
    });
  }

  public closeNotification() {
    this.notification = null;
  }
}
