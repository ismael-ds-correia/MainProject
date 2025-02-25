import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-home',
  imports: [],
  template: `
    <h1>Bem-vindo à página inicial</h1>
    <button (click)="logout()">Logout</button>
  `,
  styleUrl: './home.component.css'
})

export class HomeComponent {
  constructor(private authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
