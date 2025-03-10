import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    alert('Acesso negado! Você precisa estar autenticado para acessar esta página.');
    router.navigate(['/']);
    return false;
  }

  return true;
};

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.hasRole('admin')) {
    alert('Acesso negado! Você precisa ser um administrador para acessar esta página.');
    router.navigate(['/']);
    return false;
  }

  return true;
};
