import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { XHRBackend } from '@angular/http';

import { HomeComponent } from './';
import { CoreHttpService,
  InlineLoaderDirective } from './';
import { NotificationBannerComponent } from './notification-banner/notification-banner.component';
import { RouterModule } from '@angular/router';
import { NotificationService, EntityService } from './';

export function coreHttpFactory(backend: XHRBackend) {
  return new CoreHttpService(backend);
}

const components = [
  HomeComponent,
  NotificationBannerComponent,
  InlineLoaderDirective
]

@NgModule({
  imports: [
    CommonModule,
    RouterModule
  ],
  declarations: [components, NotificationBannerComponent],
  exports: [components]
})
export class CoreModule {
  public static forRoot(): ModuleWithProviders {
    return {
      ngModule: CoreModule,
      providers: [{
          provide: CoreHttpService,
          useFactory: coreHttpFactory,
          deps: [XHRBackend]
        },
        NotificationService,
        EntityService
     ]
    };
  }
 }
