import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MysqlComponent } from './mysql.component';
import { MysqlRoutingModule } from './mysql-routing.module';

@NgModule({
  imports: [
    CommonModule,
    MysqlRoutingModule
  ],
  declarations: [MysqlComponent]
})
export class MysqlModule { }
