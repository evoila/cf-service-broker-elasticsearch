import { Type, ModuleWithProviders, Injectable } from '@angular/core';
import { Route } from '@angular/router';

/**
 * Defines messages to display to the user when logging in
 */
@Injectable()
export class LoginMessages {
  registerSuccess: string;
  idpRegister?: string;
  idpAcceptInvite?: string;
}

/**
 * Declares which lazy modules are supported by the Panel
 */
export class ModuleSupport {
  // note: using class instead of interface to work around https://github.com/angular/angular-cli/issues/2034
  general: boolean;
  backup: boolean;
  serviceKeys: boolean;
}

export interface ModuleRouteData {
  skipPreload?: boolean;
  authDeniedMessage: keyof LoginMessages;
}

// do not confuse this with ng build --target (prod/dev) option which just sets a couple of ng-cli switches
// The BuildTarget here  refers to different variants of the panel
// NB: we use Route[] because angular-cli has only very limited capability to detect programatically computed lazy modules
export class BuildTarget {
  // note: use class to work around https://github.com/angular/angular-cli/issues/2034
  /**
   * Eagerly loaded core modules (delivered on first page load)
   */
  coreModules: (Type<any> | ModuleWithProviders)[];
  /**
   * Routes for lazy loaded core modules.
   */
  extensionModules: Route[];
}

export function supportedExtensions(bt: BuildTarget): ModuleSupport {
  const result = {};
  bt.extensionModules.forEach(x => {
    if (x.path) {
      result[x.path] = true;
    }
  });

  return <any>result;
}
