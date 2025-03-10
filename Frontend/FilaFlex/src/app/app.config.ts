import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { Routes, provideRouter } from '@angular/router';
import { authGuard, adminGuard } from './auth/guards/auth.guard';
import routes from './app.routes';

const appRoutes: Routes = [
  { path: '', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'ServiceManagement', loadComponent: () => import('./service-management/service-management.component').then(m => m.ServiceManagementComponent), canActivate: [adminGuard] }
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(appRoutes)
  ]
};
