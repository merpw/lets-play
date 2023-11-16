import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AuthService } from 'src/app/shared/auth.service';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
})
export class LoginFormComponent implements OnInit, OnDestroy {
  public form: FormGroup = new FormGroup({
    email: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  public invalidLogin = false;

  private destroy$ = new Subject<void>();

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => (this.invalidLogin = false));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  login() {
    if (this.form.invalid) {
      this.invalidLogin = true;
      return;
    }
    this.http
      .post('/api/auth/login', this.form.value, {
        observe: 'response',
        responseType: 'text',
      })
      .subscribe({
        next: (resp: any) => {
          console.log(resp);
          localStorage.setItem('token', resp.body);
          this.authService.authenticated = true;
          this.router.navigateByUrl('/');
        },
        error: (error) => {
          console.log('login did not success');
          console.log(error);
        },
      });
  }
}
