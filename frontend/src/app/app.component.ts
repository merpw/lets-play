import { Component, OnInit } from '@angular/core';
import { AuthService } from './shared/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  title = 'buy-01-frontend';

  public isInitialized = false;

  constructor(public authService: AuthService) {}
  ngOnInit(): void {
    this.authService.getAuthenticationStatus().subscribe(() => {
      this.isInitialized = true;
      console.log('auth status: ' + this.authService.isAuthenticated);
    });
  }
}
