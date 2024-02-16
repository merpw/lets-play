import { HttpClient, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { Profile } from '../models/profile.model';
import { UserDetails } from '../models/user-details.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private profileUrl = 'api/auth/profile';
  private loginUrl = '/api/auth/login';

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

  isUser(): boolean {
    return this._profile?.role === 'user';
  }

  logout() {
    this.authenticated = false;
    localStorage.removeItem('userId');
    localStorage.removeItem('token');
  }

  login(userDetails: UserDetails): Observable<any> {
    return this.http.post(this.loginUrl, userDetails, {
      observe: 'response',
      responseType: 'text',
    });
  }

  getAuthenticationStatus(): Observable<Profile | null> {
    if (!localStorage.getItem('token')) {
      this.logout();
      return of(null);
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
            this.authenticated = true;
            localStorage.setItem('userId', resp.body.id);
            this._profile = resp.body;
            return resp.body;
          }
          this.logout();
          return null;
        }),
        catchError(() => of(null))
      );
  }
}
