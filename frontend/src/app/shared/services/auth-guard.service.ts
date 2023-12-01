import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot) {
    const isAuthenticated = !!localStorage.getItem('token');
    if (isAuthenticated) {
      return true;
    }
    this.router.navigateByUrl('/login');
    return false;
  }
}
