import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private getUserUrl = 'api/users/';

  constructor(private http: HttpClient) {}

  getUsers(ids: string[]): Observable<any> {
    return forkJoin(
      ids.map((id) => this.http.get(this.getUserUrl + id), {
        observe: 'response',
        responseType: 'text',
      })
    );
  }
}
