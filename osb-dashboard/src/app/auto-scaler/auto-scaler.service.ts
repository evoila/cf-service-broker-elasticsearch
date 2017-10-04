import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

// import { environment } from 'environments/runtime-environment';
import { CoreHttpService } from '../core/core-http.service';
import { EntityService } from '../core/entity.service';

// const serviceInstanceId = environment.serviceInstanceId;
@Injectable()
export class AutoScalerService extends EntityService {
  readonly BACKUP_BASEURL = '';

  constructor(protected readonly entityService: EntityService,
    protected readonly httpService: CoreHttpService) {
    super(httpService);
    this.setCustomHeader('secret', 'jbkneo38858fjvone92');
    this.setCustomHeader('content-type', 'application/json;charset=UTF-8');
    this.setCustomHeader('accept', 'application/json;charset=UTF-8');
  }

  public loadAll(entityRel: string): Observable<{} | any> {
    return this.all(this.BACKUP_BASEURL + '/' + entityRel);
  }

  public deleteOne(entityRel: string): Observable<{} | any> {
    return this.delete(this.BACKUP_BASEURL + '/' + entityRel);
  }

  public loadOne(entityRel: string, id: string): Observable<{} | any> {
    return this.get(this.BACKUP_BASEURL + '/' + entityRel + '/' + id);
  }

  public saveOne(entity: any, entityRel: string, id?: string): Observable<{} | any> {
    if (id) {
      return this.patch(this.BACKUP_BASEURL + '/' + entityRel + '/' + id, entity);
    } else {
      return this.post(this.BACKUP_BASEURL + '/' + entityRel, entity);
    }
  }

}
