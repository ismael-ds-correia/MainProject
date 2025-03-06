import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';
import { CommonModule, CurrencyPipe, SlicePipe } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, SlicePipe],
  template: `
    <div class="home-container">
      <h1>Bem-vindo à página inicial</h1>
      <button (click)="logout()" class="logout-button">Logout</button>
      
      <div class="appointment-grid">
        <div *ngFor="let appointmentType of appointmentTypes" class="appointment-card">
          <h2>{{ appointmentType.name }}</h2>
          <p class="description">{{ appointmentType.description | slice:0:50 }}...</p>
          <p class="price">Preço: {{ appointmentType.price | currency:'BRL' }}</p>
          <p class="time">Tempo estimado: {{ appointmentType.estimatedTime }} minutos</p>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  appointmentTypes: AppointmentType[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private appointmentTypeService: AppointmentTypeService
  ) {}

  ngOnInit(): void {
    this.appointmentTypeService.getAppointmentTypes().subscribe((data) => {
      this.appointmentTypes = data;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}