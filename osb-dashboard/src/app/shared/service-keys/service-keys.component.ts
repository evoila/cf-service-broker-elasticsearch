import { Component, OnInit } from '@angular/core';
import { ServiceKeysService } from './service-keys.service';
import {NotificationService, Notification} from '../../core/notification.service';

@Component({
  selector: 'sb-service-keys',
  templateUrl: './service-keys.component.html',
  styleUrls: ['./service-keys.component.scss']
})
export class ServiceKeysComponent implements OnInit {
  readonly ENTITY = 'servicekeys';
  serviceKeys: [any];
  isLoading = false;

  constructor(protected readonly service: ServiceKeysService,
              protected readonly nService: NotificationService) { }

  ngOnInit() {
    this.loadKeys();
  }

  loadKeys(): void {
    this.service.loadAll(this.ENTITY)
    .subscribe((keys_page: any) => {
      this.serviceKeys = keys_page.content;
    });
  }

  create(): void {
    this.isLoading = true;
    this.service.saveOne({}, this.ENTITY)
      .subscribe({
        next: (d) => {
          this.isLoading = false;
          this.loadKeys();
          this.nService.add(new Notification('Warning', 'Created new Service Key.'));
        },
        error: (e) => {
          this.nService.add(new Notification('Warning', 'Could not generate new Service Key'));
        }
      });
  }
}
