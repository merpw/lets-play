import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin, map, catchError, of, EMPTY } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private getUserUrl = 'api/users/';

  constructor(private http: HttpClient) {}

  // FIXME: api not working
  getUsers(ids: string[]): Observable<any> {
    return forkJoin(
      ids.map((id) =>
        this.http.get(this.getUserUrl + id).pipe(catchError(() => of(EMPTY)))
      )
    );
  }

  getUser(id: string): Observable<any> {
    return this.http.get(this.getUserUrl + id, {
      observe: 'response',
      responseType: 'json',
    });
  }

  getUserNameById(id: string): Observable<any> {
    return this.getUser(id).pipe(
      map((resp) => resp.body.name),
      catchError(() => of(`User#${id}`))
    );
  }
}
