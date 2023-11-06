import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.scss'],
})
export class SignupFormComponent {
  public form: FormGroup = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
    email: new FormControl(''),
  });

  constructor(private http: HttpClient) {}

  submit() {
    console.log(this.form.value);
    // just for testing the nginx server
    this.http.get('/api/products').subscribe((resp) => {
      console.log(resp);
    });
  }
}
