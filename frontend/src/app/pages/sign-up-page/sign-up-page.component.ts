import { Component } from '@angular/core';
import { Result } from 'src/app/shared/models/result.model';

@Component({
  selector: 'app-sign-up-page',
  templateUrl: './sign-up-page.component.html',
  styleUrls: ['./sign-up-page.component.scss'],
})
export class SignUpPageComponent {
  isLoading = false;
  error: Result = { type: 'error', message: '' };

  showSpinner(loading: boolean) {
    console.log(loading);
    this.isLoading = loading;
  }

  showError(errorMessage: string) {
    this.error.message = errorMessage;
  }
}
