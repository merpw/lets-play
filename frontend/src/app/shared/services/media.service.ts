import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class MediaService {
  public mediaApiBaseURL = 'api/media';
  private mediaUploadURL = this.mediaApiBaseURL + '/upload';

  constructor(private http: HttpClient, private authService: AuthService) {}

  uploadMedia(file: File): Observable<HttpResponse<string>> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(this.mediaUploadURL, formData, {
      withCredentials: this.authService.isAuthenticated,
      observe: 'response',
      responseType: 'text',
    });
  }
}
