import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { PostgresqlComponent } from './postgresql.component';

export const ROUTES = [
  {
    path: '',
    component: PostgresqlComponent,
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES)],
  exports: [RouterModule]
})
export class PostgresqlRoutingModule { }
