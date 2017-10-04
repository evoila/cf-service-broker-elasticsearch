import { Injectable } from '@angular/core';
import {
  Http, ConnectionBackend, RequestOptionsArgs,
  Headers, RequestOptions, Request, Response
} from '@angular/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/mergeMap';

import { environment } from 'environments/runtime-environment';

const baseUrl = environment.baseUrls.serviceBrokerUrl;
const authToken = environment.token;

@Injectable()
export class CoreHttpService extends Http {
  private customHeaders = new Headers();
  private accessToken: string | undefined;

  public static makeOptions(accessToken?: string): RequestOptions {
    const headers = new Headers();

    headers.append('Accept', 'application/hal+json;charset=UTF-8');
    headers.append('Content-Type', 'application/hal+json;charset=UTF-8');

    if (accessToken) {
      headers.append('Authorization', accessToken);
    }

    return new RequestOptions({
      headers: headers
    });
  }

  constructor(
    backend: ConnectionBackend) {
    super(backend, CoreHttpService.makeOptions(authToken));
    this.accessToken = authToken;
  }

  public setCustomHeader(key: string, value: string) {
    this.customHeaders.append(key, value);
  }

  public request(req: Request, options?: RequestOptionsArgs): Observable<Response> {
    this.applyBaseUrl(req, options);

    if (this.accessToken) {
      req.headers.set('Authorization', this.accessToken);
    } else {
      req.headers.delete('Authorization');
    }

    this.customHeaders.forEach((values: string[], name: string) => {
        req.headers.set(name, values);
    });

    return super.request(req, options);
  }

  private applyBaseUrl(req: Request, options?: RequestOptionsArgs) {
    req.url = this.processUrl(req.url);
  }

  private processUrl(url: string): string {
    // only prepend baseUrl if it's a relative path (i.e. not a self link or similar)
    if (url.indexOf('http') !== 0) {
      return baseUrl + url;
    }

    return url;
  }

  public formatError<T>(error: Response | any): Observable<T> {
    let errMsg: string;

    if (error instanceof Response) {
      const body = error.json() || '';
      const msg = body.message || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''}. ${msg}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }

    return Observable.throw(errMsg);
  }
}
