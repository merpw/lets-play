import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {
  public form: FormGroup = new FormGroup({
    username: new FormControl('')
  });

  constructor(private http: HttpClient) {
    
  }

  submit(){
    this.http.get('/api/login', this.form.value).subscribe((resp) => {
      console.log(resp);
    })
  }
}
