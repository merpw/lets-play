import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Result } from 'src/app/shared/models/result.model';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent {
  public result: Result | null = null;
  public isLoading = false;

  constructor(private router: Router) {
    const state = this.router.getCurrentNavigation()?.extras?.state;
    if (state) {
      console.log(state['email']);
      this.result = {
        type: 'success',
        message: `Welcome, ${state['email']}!`,
      };
    }
  }

  showSpinner(isLoading: boolean) {
    this.isLoading = isLoading;
  }

  showError(error: Result | null) {
    this.result = error;
  }
}
