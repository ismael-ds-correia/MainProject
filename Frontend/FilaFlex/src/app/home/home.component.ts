import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  private authService = inject(AuthService);
  appointmentTypes = [
    { name: 'Consulta Geral', description: 'Atendimento clínico geral.', price: 150, estimatedTime: 30 },
    { name: 'Exame de Rotina', description: 'Exames periódicos para check-up.', price: 200, estimatedTime: 45 },
  ];

  logout() {
    this.authService.logout();
  }
}
