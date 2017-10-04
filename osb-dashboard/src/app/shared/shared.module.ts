import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BackupComponent } from './backup/backup.component';
import { ServiceKeysComponent } from './service-keys/service-keys.component';
import { NoContentComponent } from './no-content/no-content.component';
import { RouterModule } from '@angular/router';
import { BackupService } from './backup/backup.service';
import { CoreModule } from '../core/core.module';
import { BackupPlanComponent } from './backup/backup-plan/backup-plan.component';
import { BackupJobComponent } from './backup/backup-job/backup-job.component';
import { SharedRoutingModule } from './shared-routing.module';
import { BackupJobDetailsComponent } from './backup/backup-job/backup-job-details/backup-job-details.component';
import { RestoreJobComponent } from './backup/restore-job/restore-job.component';
import { FileEndpointComponent } from './backup/file-endpoint/file-endpoint.component';
import {GeneralComponent} from './general/general.component';
import {GeneralService} from './general/general.service';
import {ServiceKeysService} from './service-keys/service-keys.service';
import {ServiceKeysDetailComponent} from './service-keys/service-key-details/service-keys-detail.component';

const components = [BackupComponent,
  ServiceKeysComponent,
  NoContentComponent,
  BackupPlanComponent,
  BackupJobComponent,
  BackupJobDetailsComponent,
  RestoreJobComponent,
  FileEndpointComponent,
  GeneralComponent,
  ServiceKeysDetailComponent
]

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    SharedRoutingModule,
    CoreModule,
  ],
  declarations: [...components],
  exports: [components],
  providers: [BackupService, GeneralService, ServiceKeysService]
})
export class SharedModule { }
