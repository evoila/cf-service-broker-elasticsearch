import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BackupComponent } from './backup/backup.component';
import { BackupPlanComponent } from './backup/backup-plan/backup-plan.component';
import { BackupJobComponent } from './backup/backup-job/backup-job.component';
import { RestoreJobComponent } from './backup/restore-job/restore-job.component';
import { BackupJobDetailsComponent } from './backup/backup-job/backup-job-details/backup-job-details.component';
import { FileEndpointComponent } from './backup/file-endpoint/file-endpoint.component';
import {GeneralComponent} from './general/general.component';
import {ServiceKeysComponent} from './service-keys/service-keys.component';
import {ServiceKeysDetailComponent} from './service-keys/service-key-details/service-keys-detail.component';

export const ROUTES = [
  {
    path: '',
    component: GeneralComponent,
  },
  {
    path: 'backup',
    component: BackupComponent,
  },
  {
    path: 'backup/plan',
    component: BackupPlanComponent
  },
  {
    path: 'backup/plan/:planId',
    component: BackupPlanComponent
  },
  {
    path: 'backup/job',
    component: BackupJobComponent
  },
  {
    path: 'backup/job/:jobId',
    component: BackupJobDetailsComponent
  },
  {
    path: 'backup/file-endpoint',
    component: FileEndpointComponent
  },
  {
    path: 'backup/file-endpoint/:fileEndpointId',
    component: FileEndpointComponent
  },
  {
    path: 'backup/create-restore',
    component: RestoreJobComponent
  },
  {
    path: 'service-keys',
    component: ServiceKeysComponent
  },
  {
    path: 'service-keys/:serviceKeyId',
    component: ServiceKeysDetailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES)],
  exports: [RouterModule]
})
export class SharedRoutingModule { }
