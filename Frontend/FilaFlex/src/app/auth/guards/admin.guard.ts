import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service'; // Ajuste o caminho conforme necessário

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    const user = this.authService.getCurrentUser();
    
    if (user && user.role === 'ADMIN') {
      return true;
    } else {
      this.router.navigate(['/unauthorized']); // Redireciona para uma página de acesso negado
      return false;
    }
  }
}
