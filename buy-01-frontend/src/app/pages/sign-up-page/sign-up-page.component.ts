import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-sign-up-page',
  templateUrl: './sign-up-page.component.html',
  styleUrls: ['./sign-up-page.component.scss']
})
export class SignUpPageComponent {
  public form: FormGroup = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
    email: new FormControl(''),
  });

  constructor(private http: HttpClient) {
    
  }

  submit(){
    console.log(this.form.value);
    this.http.get('/auth/register', this.form.value).subscribe((resp) => {
      console.log(resp);
    })
  }
}
