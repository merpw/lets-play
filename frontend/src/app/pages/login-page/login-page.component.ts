import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent {
  message = '';
  errorMessage = '';
  isLoading = false;

  constructor(private router: Router) {
    const state = this.router.getCurrentNavigation()?.extras?.state;
    if (state) {
      console.log(state['email']);
      this.message = `Welcome, ${state['email']}!`;
    }
  }

  showSpinner(isLoading: boolean) {
    this.isLoading = isLoading;
  }

  showError(errorMessage: string) {
    this.errorMessage = errorMessage;
  }
}
