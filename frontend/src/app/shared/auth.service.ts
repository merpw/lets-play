import { HttpClient, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  CanActivateFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, catchError, map, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _isAuthenticated = false;
  private profileUrl = 'api/auth/profile';
  constructor(private http: HttpClient) {}

  get isAuthenticated(): boolean {
    return this._isAuthenticated;
  }

  set authenticated(v: boolean) {
    this._isAuthenticated = v;
  }

  getAuthenticationStatus(): Observable<boolean> {
    if (!localStorage.getItem('token')) {
      return of(false);
    }
    return this.http
      .get(this.profileUrl, {
        withCredentials: true,
        observe: 'response',
        responseType: 'json',
      })
      .pipe(
        map((resp: any) => {
          if (resp.status === HttpStatusCode.Ok) {
            console.log('user has valid token');
            console.log(resp.body);
            this.authenticated = true;
            localStorage.setItem('userId', resp.body.id);
            return true;
          }
          return false;
        }),
        catchError(() => of(false))
      );
  }
}
