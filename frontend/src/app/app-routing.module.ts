import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { SignUpPageComponent } from './pages/sign-up-page/sign-up-page.component';
import { ProductListingPageComponent } from './pages/product-listing-page/product-listing-page.component';
import { SellerProductManagementPageComponent } from './pages/seller-product-management-page/seller-product-management-page.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { AuthGuardService } from './shared/services/auth-guard.service';
import { ProductDetailsComponent } from './components/product-details/product-details.component';
import { EditProfilePageComponent } from './pages/edit-profile-page/edit-profile-page.component';

const routes: Routes = [
  {
    path: 'home',
    title: 'Welcome! :)',
    component: HomePageComponent,
  },
  {
    path: 'login',
    title: 'Login',
    component: LoginPageComponent,
  },
  {
    path: 'sign-up',
    title: 'Sign in',
    component: SignUpPageComponent,
  },
  {
    path: 'edit-profile',
    title: 'Edit profile',
    component: EditProfilePageComponent,
  },
  {
    path: 'product-listing',
    title: 'Product Listing',
    component: ProductListingPageComponent,
    // canActivate: [AuthGuardService], // can everyone see product listing or only registered users?
  },
  {
    path: 'seller-product-management',
    title: 'Seller Product Management',
    component: SellerProductManagementPageComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'product/:id',
    title: 'Product detail',
    component: ProductDetailsComponent,
  },
  {
    path: '**',
    redirectTo: 'home',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
