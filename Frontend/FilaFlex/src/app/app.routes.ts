import { Routes } from '@angular/router';
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import { HomeComponent } from './home/home.component';
import { AdminComponent } from './admin/admin.component';
import { UnauthorizedComponent } from './unauthorized/unauthorized.component';
import { ServiceManagementComponent } from './service-management/service-management.component';
import { authGuard } from './auth/guards/auth.guard';
import { loginGuard } from './auth/guards/login.guard';
import { AppointmentTypeDetailsComponent } from './appointment-type-details/appointment-type-details.component';
import { AppointmentSchedulingComponent } from './appointment-scheduling/appointment-scheduling.component';
import { AppointmentManagementComponent } from './appointment-management/appointment-management.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent, canActivate: [loginGuard] },
  { path: 'login', component: LoginComponent, canActivate: [loginGuard] },
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [authGuard], data: { role: 'ADMIN' } },
  { path: 'service-management', component: ServiceManagementComponent, canActivate: [authGuard] },
  { path: 'appointment-details/:name', component: AppointmentTypeDetailsComponent, canActivate: [authGuard] },
  { path: 'unauthorized', component: UnauthorizedComponent },
  { path: 'appointment-scheduling', component: AppointmentSchedulingComponent },
  { path: 'appointment-management', component: AppointmentManagementComponent, canActivate: [authGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

export default routes;