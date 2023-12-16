import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanActivate {
  constructor(private router: Router) {}

  canActivate() {
    const isAuthenticated = !!localStorage.getItem('token');
    if (isAuthenticated) {
      return true;
    }
    this.router.navigateByUrl('/login');
    return false;
  }
}
