import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MysqlComponent } from './mysql.component';

export const ROUTES = [
  {
    path: '',
    component: MysqlComponent,
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES)],
  exports: [RouterModule]
})
export class MysqlRoutingModule { }
