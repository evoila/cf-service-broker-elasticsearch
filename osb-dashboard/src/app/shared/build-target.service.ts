import { Injectable } from '@angular/core';

import {
  BuildTarget, ModuleSupport, supportedExtensions
} from 'environments/build-target';

// reexport it here so that others can consume it from app/shared
export {
  LoginMessages, ModuleSupport, ModuleRouteData
} from 'environments/build-target';

/**
 * Note: this service must be provided from the AppModule
 * This is necessary to avoid dependency cycles between buildTargets and consumers
 * of buildTarget information
 */
@Injectable()
export class BuildTargetService {
  public readonly moduleSupport: ModuleSupport;
  public readonly dynamicModuleSupport: any;
  constructor(bt: BuildTarget) {
    this.dynamicModuleSupport = supportedExtensions(bt);
  }
}
