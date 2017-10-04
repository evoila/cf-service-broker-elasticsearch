import { Component, OnInit } from '@angular/core';
import { BackupService } from '../backup.service';

@Component({
  selector: 'sb-backup-job',
  templateUrl: './backup-job.component.html',
  styleUrls: ['./backup-job.component.scss']
})
export class BackupJobComponent implements OnInit {
  request: any = { destination: {} }

  constructor(protected readonly backupService: BackupService) { }

  ngOnInit() {
  }

  onSubmit() {
    this.backupService.saveOne('backup', this.request)
      .subscribe((job: any) => {
      });
  }

}
