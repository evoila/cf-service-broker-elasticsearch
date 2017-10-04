import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MongoDbComponent } from './mongodb.component';
import { MongoDbRoutingModule } from './mongodb-routing.module';

@NgModule({
  imports: [
    CommonModule,
    MongoDbRoutingModule
  ],
  declarations: [MongoDbComponent]
})
export class MongoDbModule { }
