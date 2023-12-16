import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-edit-profile-page',
  templateUrl: './edit-profile-page.component.html',
  styleUrls: ['./edit-profile-page.component.scss'],
})
export class EditProfilePageComponent {
  isLoading = false;
  result: Result | undefined;

  private editProfileURL = '/api/users';

  constructor(private http: HttpClient, public authService: AuthService) {}

  showSpinner(loading: boolean) {
    console.log(loading);
    this.isLoading = loading;
  }

  showError(errorMessage: string) {
    this.result = {
      type: 'error',
      message: errorMessage,
    };
  }

  updateProfile(userDetails: any) {
    this.result = undefined;
    if (!this.authService.profile || !this.authService.profile.id) {
      this.result = {
        type: 'error',
        message: 'Update profile goes wrong.',
      };
      return;
    }
    this.http
      .put(
        this.editProfileURL + '/' + this.authService.profile?.id,
        userDetails,
        {
          withCredentials: true,
        }
      )
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (resp: any) => {
          console.log(resp);
          this.result = {
            type: 'success',
            message: 'Your profile is updated successfully.',
          };
        },
        error: (error) => {
          console.log(error);
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Edit profile went wrong. Please try again.';
          console.log(errorMessage);
          this.result = {
            type: 'error',
            message: errorMessage,
          };
        },
      });
  }
}
