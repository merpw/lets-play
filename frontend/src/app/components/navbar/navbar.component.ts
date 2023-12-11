import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent {
  constructor(public authService: AuthService, private router: Router) {}
  logout() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
