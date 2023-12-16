import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { finalize } from 'rxjs';
import { Profile } from 'src/app/shared/models/profile.model';
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
    this.isLoading = loading;
  }

  showError(errorMessage: string) {
    this.result = {
      type: 'error',
      message: errorMessage,
    };
  }

  updateProfile(userDetails: Profile) {
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
        next: () => {
          this.result = {
            type: 'success',
            message: 'Your profile is updated successfully.',
          };
          this.authService.getAuthenticationStatus().subscribe();
        },
        error: (error) => {
          const errorMessage =
            error.error?.detail || 'Edit profile went wrong. Please try again.';
          this.result = {
            type: 'error',
            message: errorMessage,
          };
        },
      });
  }
}
