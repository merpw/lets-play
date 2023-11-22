import { Component } from '@angular/core';

@Component({
  selector: 'app-sign-up-page',
  templateUrl: './sign-up-page.component.html',
  styleUrls: ['./sign-up-page.component.scss'],
})
export class SignUpPageComponent {
  isLoading = false;
  errorMessage = '';

  showSpinner(loading: boolean) {
    console.log(loading);
    this.isLoading = loading;
  }

  showError(errorMessage: string) {
    this.errorMessage = errorMessage;
  }
}
