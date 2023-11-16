import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, mergeMap, of, switchMap, tap } from 'rxjs';
import { AuthService } from 'src/app/shared/auth.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss'],
})
export class HomePageComponent implements OnInit {
  private profileUrl = 'api/auth/profile';
  private productsUrl = 'api/products';
  public profile: {
    email: string;
    id: string;
    role: string;
    name: string;
  } = {
    email: '',
    id: '',
    role: '',
    name: '',
  };

  public products = new Array();
  constructor(
    public authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}
  ngOnInit(): void {
    this.http
      .get(this.profileUrl, {
        observe: 'response',
        withCredentials: true,
        responseType: 'json',
      })
      .pipe(
        catchError((resp) => {
          console.log(resp);
          return of(null);
        }),
        tap((resp: any) => {
          if (resp !== null) {
            this.profile = resp.body;
            localStorage.setItem('userId', resp.body.id);
          }
        }),
        switchMap(() =>
          this.http.get(this.productsUrl, {
            observe: 'response',
          })
        )
      )
      .subscribe({
        next: (resp: any) => {
          console.log(resp);
          this.products = resp.body;
        },
        error: (error) => {
          console.log('error in fetching products');
          console.log(error);
        },
      });
  }
}
