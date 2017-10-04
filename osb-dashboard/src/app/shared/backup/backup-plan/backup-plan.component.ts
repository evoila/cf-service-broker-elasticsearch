import { Component, OnInit } from '@angular/core';
import { BackupService } from '../backup.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationService, Notification} from '../../../core/notification.service';


@Component({
  selector: 'sb-backup-plan',
  templateUrl: './backup-plan.component.html',
  styleUrls: ['./backup-plan.component.scss']
})
export class BackupPlanComponent implements OnInit {
  readonly ENTITY: string = 'plans';
  plan: any = {}
  destinations: any = [];
  update = false;

  constructor(protected readonly backupService: BackupService,
              protected readonly route: ActivatedRoute,
              protected readonly router: Router,
              protected readonly nService: NotificationService) { }

  ngOnInit() {
    this.backupService.loadAll('destinations')
      .subscribe(
        (dest: any) => { this.destinations = dest.content},
      );

    this.route.params.subscribe(params => {
      console.log(params['planId']);
       if (params['planId']) {
         this.update = true;
          this.backupService.loadOne(this.ENTITY, params['planId'])
            .subscribe(
              (plan: any) => { this.plan = plan},
            );
       }
    });

  }

  delete(): void {
    this.backupService.deleteOne(this.ENTITY, this.plan)
      .subscribe((plan: any) => {
        this.redirect();
      });
  }

  onSubmit(): void {
    const id = this.update ? this.plan.id : null;
    this.backupService.saveOne(this.plan, this.ENTITY, id)
      .subscribe({
        next: (d) => {
          this.nService.add(new Notification('Warning', 'Backup Plan Created'));
          this.redirect();
        },
        error: (e) => {
          this.nService.add(new Notification('Warning', 'Could not create Backup Plan. Please check your entries.'));
        }
      });
  }

  private redirect(): void {
    this.router.navigate(['/backup']);
  }

}
