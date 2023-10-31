import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-sign-up-page',
  templateUrl: './sign-up-page.component.html',
  styleUrls: ['./sign-up-page.component.scss'],
})
export class SignUpPageComponent {
  public form: FormGroup = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
    email: new FormControl(''),
  });

  constructor(private http: HttpClient) {}

  submit() {
    console.log(this.form.value);
    // just for testing the nginx server
    this.http.get('/products').subscribe((resp) => {
      console.log(resp);
    });
  }
}
