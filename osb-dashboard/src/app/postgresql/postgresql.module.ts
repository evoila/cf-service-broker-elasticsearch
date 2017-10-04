import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostgresqlComponent } from './postgresql.component';
import { PostgresqlRoutingModule } from './postgresql-routing.module';

@NgModule({
  imports: [
    CommonModule,
    PostgresqlRoutingModule
  ],
  declarations: [PostgresqlComponent]
})
export class PostgresqlModule { }
