import { Component, OnInit, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Location } from '@angular/common';
import { NgIf, NgFor } from '@angular/common';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-appointment-management',
  standalone: true,
  templateUrl: './appointment-management.component.html',
  styleUrls: ['./appointment-management.component.css'],
  imports: [NgIf, NgFor, CommonModule] // Isso garante que *ngIf e *ngFor funcionem sem precisar de módulos
})
export class AppointmentManagementComponent implements OnInit {
  loading = signal(false);
  error = signal<string | null>(null);
  appointments = signal<any[]>([]);
  selectedAppointment = signal<any | null>(null);

  constructor(private http: HttpClient, private location: Location) {}

  ngOnInit() {
    this.fetchAppointments();
  }

  fetchAppointments() {
    this.loading.set(true);
    this.http.get<any[]>('http://localhost:8080/appointment/all', this.getHeaders())
      .subscribe({
        next: (data) => {
          this.appointments.set(Array.isArray(data) ? data : []);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Erro ao buscar agendamentos:', err);
          this.error.set('Erro ao carregar agendamentos. Verifique a API.');
          this.loading.set(false);
        },
      });
  }

  editAppointment(appointment: any) {
    this.selectedAppointment.set({ ...appointment });
  }

  updateField(field: string, event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const currentAppointment = this.selectedAppointment();
    if (currentAppointment) {
      if (field === 'scheduledDateTime') {
        const [date] = currentAppointment.scheduledDateTime?.split('T') || ['', ''];
        currentAppointment.scheduledDateTime = `${date}T${inputElement.value}:00`;
      }
      this.selectedAppointment.set(currentAppointment);
    }
  }

  saveChanges() {
    const appointment = this.selectedAppointment();
    if (!appointment) return;

    this.http.put(
      `http://localhost:8080/appointment/${appointment.id}`,
      appointment,
      this.getHeaders()
    ).subscribe({
      next: () => {
        this.fetchAppointments();
        this.selectedAppointment.set(null);
      },
      error: (err) => {
        console.error('Erro ao atualizar:', err);
        this.error.set('Erro ao atualizar agendamento. Tente novamente.');
      },
    });
  }

  deleteAppointment(id: number) {
    this.http.delete(`http://localhost:8080/appointment/${id}`, this.getHeaders())
      .subscribe({
        next: () => this.fetchAppointments(),
        error: (err) => {
          console.error('Erro ao excluir:', err);
          this.error.set('Erro ao excluir agendamento.');
        },
      });
  }

  goBack() {
    this.location.back();
  }

  getHeaders() {
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json',
      }),
    };
  }
}
