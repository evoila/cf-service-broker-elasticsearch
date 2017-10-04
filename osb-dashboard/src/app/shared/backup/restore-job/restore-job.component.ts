import { Component, OnInit } from '@angular/core';
import { BackupService } from '../backup.service';

@Component({
  selector: 'sb-restore-job',
  templateUrl: './restore-job.component.html',
  styleUrls: ['./restore-job.component.scss']
})
export class RestoreJobComponent implements OnInit {
  request: any = { source: {} }

  constructor(protected readonly backupService: BackupService) { }

  ngOnInit() {}

  onSubmit() {
    this.backupService.saveOne('restore', this.request)
      .subscribe((job: any) => {
      });
  }
}
