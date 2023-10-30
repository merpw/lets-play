import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { SignUpPageComponent } from './pages/sign-up-page/sign-up-page.component';
import { SellerProductManagementPageComponent } from './pages/seller-product-management-page/seller-product-management-page.component';
import { ProductListingPageComponent } from './pages/product-listing-page/product-listing-page.component';
import { MediaManagementPageComponent } from './pages/seller-product-management-page/media-management-page/media-management-page.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoginFormComponent } from './pages/login-page/login-form/login-form.component';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import { SignupFormComponent } from './pages/sign-up-page/signup-form/signup-form.component';
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
    SignupFormComponent
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
    MatInputModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
