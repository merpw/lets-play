import { Component, OnInit } from '@angular/core';
import { Profile } from 'src/app/shared/models/profile.model';
import { AuthService } from 'src/app/shared/services/auth.service';
import { MediaService } from 'src/app/shared/services/media.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss'],
})
export class HomePageComponent implements OnInit {
  public profile!: Profile;

  constructor(
    public authService: AuthService,
    public mediaService: MediaService
  ) {}

  ngOnInit(): void {
    this.authService
      .getAuthenticationStatus()
      .subscribe((profile: Profile | null) => {
        if (!profile) {
          console.log('user is not logged in');
          return;
        }
        this.profile = profile;
      });
  }
}
