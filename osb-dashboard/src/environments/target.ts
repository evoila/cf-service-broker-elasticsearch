import { BuildTarget } from './build-target';

// this file is replaced by angular-cli during build, it's only a reference file during development

// We use angular-cli environments for compile time configuration only (e.g. changing modules so that AOT+tree shake are maximally effective)
// Other configuration is injected via runtime-environment.ts + index.html + Server-Side-Include trickery

/**
 * Unless you're the AppModule or AppRoutingModule, you should probably not import this.
 * Instead, use shared/BuildTargetService. Otherwise you may experience cyclic dependency issues.
 */
export const buildTarget: BuildTarget = <any>{};
