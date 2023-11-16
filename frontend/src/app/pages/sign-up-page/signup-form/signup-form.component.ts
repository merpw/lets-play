import { HttpClient, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.scss'],
})
export class SignupFormComponent {
  private signUpURL = '/api/auth/register';

  public form: FormGroup = new FormGroup({
    name: new FormControl(''),
    password: new FormControl(''),
    email: new FormControl(''),
  });

  constructor(private http: HttpClient) {}

  submit() {
    console.log(this.form.value);
    // just for testing the nginx server
    this.http
      .post(this.signUpURL, this.form.value, {
        observe: 'response',
        responseType: 'text',
      })
      .subscribe({
        next: (resp: HttpResponse<any>) => {
          console.log(resp.body);
          localStorage.setItem('userId', resp.body);
        },
        error: (error) => {
          console.log('error in signing up');
          console.log(error);
        },
      });
  }
}
