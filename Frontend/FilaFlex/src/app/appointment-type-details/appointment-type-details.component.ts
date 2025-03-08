import { Component, OnInit } from '@angular/core';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-appointment-type-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './appointment-type-details.component.html',
  styleUrl: './appointment-type-details.component.css'
})

export class AppointmentTypeDetailsComponent implements OnInit {
  appointmentType: AppointmentType | null = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private appointmentTypeService: AppointmentTypeService
  ) {}

  ngOnInit() {
    const name = this.route.snapshot.paramMap.get('name');
    if (name) {
      this.loadAppointmentTypeDetails(name);
    } else {
      this.error = "Nome do serviço não especificado";
      this.loading = false;
    }
  }

  loadAppointmentTypeDetails(name: string) {
    this.appointmentTypeService.getAppointmentTypeByName(name)
      .subscribe({
        next: (data) => {
          console.log('Dados completos recebidos:', data);
          if (data.adress) {
            console.log('Endereço encontrado:', data.adress);
          } else {
            console.log('Endereço não encontrado na resposta');
          }
          this.appointmentType = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = "Erro ao carregar detalhes do serviço";
          this.loading = false;
          console.error('Erro:', err);
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }
}