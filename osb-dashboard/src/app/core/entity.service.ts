import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { CoreHttpService } from './core-http.service';

@Injectable()
export class EntityService {

  constructor(protected readonly httpService: CoreHttpService) {}

  protected setCustomHeader(key: string, value: string) {
    this.httpService.setCustomHeader(key, value);
  }

  protected get(url: string): Observable<{} | any> {
    return this.httpService
      .get(url)
      .map(res => {
        return res.json() as any;
      })
      .catch(e => this.httpService.formatError(e));
  }

  protected delete(url: string): Observable<{} | any> {
    return this.httpService
      .delete(url)
      .map(res => {
        return res.json() as any;
      })
      .catch(e => this.httpService.formatError(e));
  }

  protected all(url: string): Observable<{} | any> {
    return this.httpService
      .get(url)
      .map(res => {
        return res.json() as any;
      })
      .catch(e => this.httpService.formatError(e));
  }

  protected post(url: string, entity: any): Observable<{} | any> {
    return this.httpService
      .post(url, entity)
      .map(res => {
        return res.json() as any;
      })
      .catch(e => this.httpService.formatError(e));
  }

  protected patch(url: string, entity: any): Observable<{} | any> {
    return this.httpService
      .patch(url, entity)
      .map(res => {
        return res.json() as any;
      })
      .catch(e => this.httpService.formatError(e));
  }

}
