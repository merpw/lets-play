import { HttpClient, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { Profile } from '../models/profile.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private profileUrl = 'api/auth/profile';

  private _isAuthenticated = false;
  private _profile: Profile | null = null;
  constructor(private http: HttpClient) {}

  get isAuthenticated(): boolean {
    return this._isAuthenticated;
  }

  set authenticated(v: boolean) {
    if (!v) {
      this._profile = null;
    }
    this._isAuthenticated = v;
  }

  get profile(): Profile | null {
    return this._profile;
  }

  hasSellerRight(): boolean {
    return this._profile?.role === 'admin' || this._profile?.role === 'seller';
  }

  logout() {
    this.authenticated = false;
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
  }

  getAuthenticationStatus(): Observable<any> {
    if (!localStorage.getItem('token')) {
      this.logout();
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
            this._profile = resp.body;
            return resp.body;
          }
          this.logout();
          return false;
        }),
        catchError(() => of(false))
      );
  }
}
