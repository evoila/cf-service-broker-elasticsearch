import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import { EntityService, CoreHttpService } from 'app/core';

import { environment } from 'environments/runtime-environment';


const serviceInstanceId = environment.serviceInstanceId;
@Injectable()
export class BackupService extends EntityService {
  SORT_CONFIG = 'sort=startDate,desc';
  BACKUP_BASEURL = '/v2/manage/backup';

  constructor(protected readonly httpService: CoreHttpService) {
    super(httpService);
  }

  public loadAll(entityRel: string): Observable<{} | any> {
    return this.all(this.BACKUP_BASEURL + '/' + serviceInstanceId + '/' + entityRel + '?' + this.SORT_CONFIG);
  }

  public deleteOne(entityRel: string, entity: any): Observable<{} | any> {
    return this.delete(this.BACKUP_BASEURL + '/' + serviceInstanceId + '/' + entityRel + '/' + entity.id);
  }

  public loadOne(entityRel: string, id: string): Observable<{} | any> {
    return this.get(this.BACKUP_BASEURL + '/' + serviceInstanceId + '/' + entityRel + '/' + id);
  }

  public saveOne(entity: any, entityRel: string, id?: string): Observable<{} | any> {
    if (id) {
      return this.patch(this.BACKUP_BASEURL + '/' + serviceInstanceId + '/' + entityRel + '/' + id, entity);
    } else {
      return this.post(this.BACKUP_BASEURL + '/'  + serviceInstanceId + '/'  + entityRel, entity);
    }
  }

  public validate(rel: string, entity: any): Observable<{} | any> {
    return this.post(this.BACKUP_BASEURL + '/' + serviceInstanceId + '/' + rel + '/validate' , entity);
  }

}
