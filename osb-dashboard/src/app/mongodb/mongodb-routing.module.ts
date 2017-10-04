import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MongoDbComponent } from './mongodb.component';

export const ROUTES = [
  {
    path: '',
    component: MongoDbComponent,
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES)],
  exports: [RouterModule]
})
export class MongoDbRoutingModule { }
