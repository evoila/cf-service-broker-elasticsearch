import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AutoScalerComponent } from './auto-scaler.component';
import { BindingComponent } from './binding/binding.component';

// Because it is loaded from the external module we don't need
// to defined the root url 'autoscaler'
export const ROUTES = [
  {
    path: '',
    component: AutoScalerComponent
  },
  {
    path: 'binding',
    component: BindingComponent
  },
  {
    path: 'binding/:bindingId',
    component: BindingComponent
  },
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES)],
  exports: [RouterModule]
})
export class AutoScalerRoutingModule { }
