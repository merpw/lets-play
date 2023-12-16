import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Profile } from 'src/app/shared/models/profile.model';
import { UserDetails } from 'src/app/shared/models/user-details.model';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.scss'],
})
export class SignupFormComponent implements OnInit {
  @Output() isLoading = new EventEmitter<boolean>();
  @Output() hasError = new EventEmitter<string>();
  @Output() userdetails = new EventEmitter<UserDetails>();

  @Input() profile?: Profile | null;

  public imageUploaded = '';

  public form: FormGroup = new FormGroup({
    name: new FormControl('', [
      Validators.required,
      Validators.maxLength(50),
      Validators.min(3),
    ]),
    password: new FormControl('', [
      Validators.required,
      Validators.maxLength(50),
      Validators.min(8),
    ]),
    email: new FormControl('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(320),
      Validators.minLength(3),
    ]),
    role: new FormControl('', Validators.required),
    image: new FormControl(''),
  });

  public roles = ['user', 'seller'];

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    if (this.profile) {
      this.form.patchValue(this.profile);
      this.imageUploaded = this.profile.image || '';
      this.form.removeControl('password');
      this.form.removeControl('role');
    }
  }

  onImageUpload(imageId: string) {
    this.imageUploaded = imageId;
  }

  onImageDelete() {
    this.imageUploaded = '';
  }
  submit() {
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
    this.userdetails.emit(this.form.value);
  }
}
