import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { Result } from 'src/app/shared/models/result.model';
import { UserDetails } from 'src/app/shared/models/user-details.model';

@Component({
  selector: 'app-sign-up-page',
  templateUrl: './sign-up-page.component.html',
  styleUrls: ['./sign-up-page.component.scss'],
})
export class SignUpPageComponent {
  isLoading = false;
  error: Result = { type: 'error', message: '' };

  private signUpURL = '/api/auth/register';

  constructor(private http: HttpClient, private router: Router) {}

  showSpinner(loading: boolean) {
    this.isLoading = loading;
  }

  showError(errorMessage: string) {
    this.error.message = errorMessage;
  }

  submitSignup(userDetails: UserDetails) {
    this.error.message = '';
    this.http
      .post(this.signUpURL, userDetails, {
        observe: 'response',
        responseType: 'text',
      })
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (resp: HttpResponse<any>) => {
          localStorage.setItem('userId', resp.body);
          this.router.navigate(['/login'], {
            state: { email: userDetails.email },
          });
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Sign up went wrong. Please try again.';
          this.error.message = errorMessage;
        },
      });
  }
}
