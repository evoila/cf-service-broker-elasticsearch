import { Component, OnInit } from '@angular/core';
import { Configuration } from '../domain/configuration';
import { SliderConfiguration } from '../domain/slider-configuration';
import { AutoScalerService } from '../auto-scaler.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationService, Notification } from '../../core/notification.service';

@Component({
  selector: 'sb-binding',
  templateUrl: './binding.component.html',
  styleUrls: ['./binding.component.scss']
})
export class BindingComponent implements OnInit {
  update = false;
  isLoading = false;
  configuration: Configuration | any = {
    scaling : {
      minMaxRange: [10, 20]
    },
    cpu: {
      lowerUpperLimit: [10, 80]
    },
    ram: {},
    latency: {},
    requests: {},
    learning: {}
  };
  readonly minMaxConfig = SliderConfiguration.MIN_MAX_CONFIG;
  readonly lowerUpperConfig = SliderConfiguration.LOWER_UPPER_CONFIG;
  readonly ENTITY = 'bindings';
  BINDING_ID;

  constructor(protected readonly asService: AutoScalerService,
              protected readonly route: ActivatedRoute,
              protected readonly router: Router,
              protected readonly nService: NotificationService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
       if (params['bindingId']) {
          this.BINDING_ID = params['bindingId'];
          this.update = true;
          this.loadEntity();
       }
    });
  }

  loadEntity() {
    this.asService.loadOne(this.ENTITY, this.BINDING_ID)
      .subscribe((configuration: any) => {
        this.configuration = this.parseLimits(configuration);
      });
  }

  onSubmit() {
    this.isLoading = true;
    this.configuration = this.unparseLimit(this.configuration);
    if (!this.update) {
      this.asService.saveOne(this.configuration, this.ENTITY)
        .subscribe((configuration: any) => {
          this.isLoading = false;
          this.router.navigate(['/autoscaler']);
      });
    } else {
      this.asService.saveOne(this.configuration, this.ENTITY, this.BINDING_ID)
        .subscribe((configuration: any) => {
          this.isLoading = false;
          this.router.navigate(['/autoscaler']);
      });
    }
  }

  // /info/{appId}/resetLST
  resetLearningTime(): void {
    this.isLoading = true;
    this.asService.saveOne({}, this.ENTITY, this.BINDING_ID + '/resetLST')
      .subscribe((configuration: any) => {
        this.nService.add(new Notification('Warning', 'Successfully reset Learning Time'));
        this.isLoading = false;
        this.loadEntity();
    });
  }

  // /info/{appId}/resetQuotient
  resetQuotient(): void {
    this.isLoading = true;
    this.asService.saveOne({}, this.ENTITY, this.BINDING_ID + '/resetQuotient')
      .subscribe((configuration: any) => {
        this.isLoading = false;
        this.loadEntity();
    });
  }

  private parseLimits(configuration: Configuration | any): void {
    configuration.cpu.lowerUpperLimit = [configuration.cpu.lowerLimit, configuration.cpu.upperLimit];
    configuration.scaling.minMaxRange = [configuration.scaling.minInstances, configuration.scaling.maxInstances];
    return configuration;
  }

  private unparseLimit(configuration: Configuration | any): void {
    if (configuration.scaling !== null && configuration.scaling.minMaxRange != null) {
      configuration.scaling.minInstances = configuration.scaling.minMaxRange[0];
      configuration.scaling.maxInstances = configuration.scaling.minMaxRange[1];
    }

    if (configuration.cpu !== null && configuration.cpu.lowerUpperLimit != null) {
      configuration.cpu.lowerLimit = configuration.cpu.lowerUpperLimit[0];
      configuration.cpu.upperLimit = configuration.cpu.lowerUpperLimit[1];
    }

    return configuration;
  }

}
