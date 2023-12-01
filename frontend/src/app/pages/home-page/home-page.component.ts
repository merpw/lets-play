import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, mergeMap, of, switchMap, tap } from 'rxjs';
import { Profile } from 'src/app/shared/models/profile';
import { AuthService } from 'src/app/shared/services/auth.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss'],
})
export class HomePageComponent implements OnInit {
  public profile!: Profile;

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getAuthenticationStatus().subscribe((profile) => {
      if (!profile) {
        console.log('user is not logged in');
        return;
      }
      this.profile = profile;
    });
  }
}
