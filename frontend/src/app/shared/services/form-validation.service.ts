import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FormValidationService {
  constructor() {}

  validateImageUpload(file: File | null | undefined): string {
    if (!file) {
      return 'File does not exist';
    }
    if (file.size > 2 * 1024 * 1024) {
      return 'File size is too big. The limit is 2MB';
    }
    const fileTypes = ['image/jpeg', 'image/gif', 'image/png', 'image/webp'];
    if (!fileTypes.includes(file.type.toLowerCase())) {
      return `File type ${file.type} is not accepted. Please upload jpeg, gif, png or webp.`;
    }
    return '';
  }
}
