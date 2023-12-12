import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { FormValidationService } from 'src/app/shared/services/form-validation.service';
import { MediaService } from 'src/app/shared/services/media.service';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.scss'],
})
export class SignupFormComponent {
  @Output() isLoading = new EventEmitter<boolean>();
  @Output() hasError = new EventEmitter<string>();

  private signUpURL = '/api/auth/register';
  public imageUploaded = '';

  public form: FormGroup = new FormGroup({
    name: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    role: new FormControl('', Validators.required),
    image: new FormControl(''),
  });

  public roles = ['USER', 'SELLER'];

  constructor(private http: HttpClient, private router: Router) {}

  onImageUpload(imageId: string) {
    this.imageUploaded = imageId;
  }

  onImageDelete(imageId: string) {
    this.imageUploaded = '';
  }
  submit() {
    console.log(this.form.value);
    // just for testing the nginx server
    this.isLoading.emit(true);
    this.hasError.emit('');

    if (this.form.invalid) {
      this.hasError.emit(
        'Your inputs are invalid. Please verify and try again.'
      );
      this.isLoading.emit(false);
      return;
    }
    this.form.controls['image'].setValue(this.imageUploaded || undefined);
    this.http
      .post(this.signUpURL, this.form.value, {
        observe: 'response',
        responseType: 'text',
      })
      .pipe(finalize(() => this.isLoading.emit(false)))
      .subscribe({
        next: (resp: HttpResponse<any>) => {
          console.log(resp.body);
          localStorage.setItem('userId', resp.body);
          this.router.navigate(['/login'], {
            state: { email: this.form.controls['email'].value },
          });
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Sign up went wrong. Please try again.';
          console.log(errorMessage);
          this.hasError.emit(errorMessage);
        },
      });
  }
}
