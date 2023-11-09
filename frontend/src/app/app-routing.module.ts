import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { SignUpPageComponent } from './pages/sign-up-page/sign-up-page.component';
import { ProductListingPageComponent } from './pages/product-listing-page/product-listing-page.component';
import { SellerProductManagementPageComponent } from './pages/seller-product-management-page/seller-product-management-page.component';
import { HomePageComponent } from './pages/home-page/home-page.component';

const routes: Routes = [
  {
    path: '',
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
  },
  {
    path: 'seller-product-management',
    title: 'Seller Product Management',
    component: SellerProductManagementPageComponent,
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
