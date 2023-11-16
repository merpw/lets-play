import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    console.log('interceptted');
    if (request.withCredentials) {
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('user has no valid token');
        this.router.navigateByUrl('/');
      }
      console.log('user is trying to access with token');
      console.log(token);
      const modifiedRequest = request.clone({
        setHeaders: {
          Authorization: 'Bearer ' + token,
        },
      });
      return next.handle(modifiedRequest);
    }
    return next.handle(request);
  }
}
