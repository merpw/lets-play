import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { SignUpPageComponent } from './pages/sign-up-page/sign-up-page.component';
import { SellerProductManagementPageComponent } from './pages/seller-product-management-page/seller-product-management-page.component';
import { ProductListingPageComponent } from './pages/product-listing-page/product-listing-page.component';
import { MediaManagementPageComponent } from './pages/seller-product-management-page/media-management-page/media-management-page.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoginFormComponent } from './pages/login-page/login-form/login-form.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { SignupFormComponent } from './pages/sign-up-page/signup-form/signup-form.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule } from '@angular/material/dialog';
import { AuthInterceptor } from './shared/auth.interceptor';
import { AddProductModalComponent } from './pages/product-listing-page/add-product-modal/add-product-modal.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    SignUpPageComponent,
    SellerProductManagementPageComponent,
    ProductListingPageComponent,
    MediaManagementPageComponent,
    NavbarComponent,
    LoginFormComponent,
    SignupFormComponent,
    HomePageComponent,
    AddProductModalComponent,
    SpinnerComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    MatInputModule,
    MatTableModule,
    MatDialogModule,
    MatProgressSpinnerModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
