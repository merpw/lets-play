import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './shared/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'buy-01-frontend';

  constructor(
    private http: HttpClient,
    private router: Router,
    public authService: AuthService
  ) {}
  ngOnInit(): void {
    this.authService.getAuthenticationStatus().subscribe(() => {
      console.log('auth status: ' + this.authService.isAuthenticated);
    });
  }
}
