import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { SignUpPageComponent } from './pages/sign-up-page/sign-up-page.component';
import { ProductListingPageComponent } from './pages/product-listing-page/product-listing-page.component';
import { SellerProductManagementPageComponent } from './pages/seller-product-management-page/seller-product-management-page.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { AuthGuardService } from './shared/auth-guard.service';

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
    path: 'product-listing',
    title: 'Product Listing',
    component: ProductListingPageComponent,
    canActivate: [AuthGuardService],
  },
  {
    path: 'seller-product-management',
    title: 'Seller Product Management',
    component: SellerProductManagementPageComponent,
    canActivate: [AuthGuardService],
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
