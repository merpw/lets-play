import { HttpClient } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, finalize, takeUntil } from 'rxjs';
import { Result } from 'src/app/shared/models/result.model';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
})
export class LoginFormComponent implements OnInit, OnDestroy {
  @Output() isLoading = new EventEmitter<boolean>();
  @Output() hasError = new EventEmitter<Result | null>();

  public form: FormGroup = new FormGroup({
    email: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  private destroy$ = new Subject<void>();

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.hasError.emit(null));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  login() {
    if (this.form.invalid) {
      this.hasError.emit({ type: 'error', message: 'Login form is invalid.' });
      return;
    }
    this.isLoading.emit(true);

    this.http
      .post('/api/auth/login', this.form.value, {
        observe: 'response',
        responseType: 'text',
      })
      .pipe(finalize(() => this.isLoading.emit(false)))
      .subscribe({
        next: (resp: any) => {
          console.log(resp);
          localStorage.setItem('token', resp.body);
          this.authService.authenticated = true;
          this.router.navigateByUrl('/');
        },
        error: (error) => {
          const errorMessage =
            JSON.parse(error.error)?.detail ??
            'Log in went wrong. Please try again.';
          console.log(errorMessage);
          this.hasError.emit({ type: 'error', message: errorMessage });
        },
      });
  }
}
