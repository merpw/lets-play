import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MediaService {
  public mediaApiBaseURL = 'api/media';
  private mediaUploadURL = this.mediaApiBaseURL + '/upload';

  constructor(private http: HttpClient) {}

  uploadMedia(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(this.mediaUploadURL, formData, {
      withCredentials: true,
      observe: 'response',
      responseType: 'text',
    });
  }
}
