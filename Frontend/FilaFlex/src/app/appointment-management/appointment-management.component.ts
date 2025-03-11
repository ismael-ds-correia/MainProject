import { Component, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Location } from '@angular/common';
import { NgIf, NgFor } from '@angular/common';
import { CommonModule } from '@angular/common';
import { AppointmentService, AppointmentSchedule } from '../services/appointment.service';

@Component({
  selector: 'app-appointment-management',
  standalone: true,
  templateUrl: './appointment-management.component.html',
  styleUrls: ['./appointment-management.component.css'],
  imports: [NgIf, NgFor, CommonModule]
})
export class AppointmentManagementComponent implements OnInit {
  loading = signal(false);
  error = signal<string | null>(null);
  appointments = signal<any[]>([]);
  selectedAppointment = signal<any | null>(null);

  constructor(
    private appointmentService: AppointmentService,
    private location: Location
  ) {}

  ngOnInit() {
    this.fetchAppointments();
  }

  fetchAppointments() {
    this.loading.set(true);
    this.error.set(null);
    
    this.appointmentService.getAppointments().subscribe({
      next: (data) => {
        console.log('Dados brutos recebidos:', data);
        
        // Processar e enriquecer os dados
        const processedData = data.map(appointment => ({
          ...appointment,
          // Adicionar propriedades formatadas para exibição
          displayUserName: this.getUserName(appointment),
          displayDate: this.formatDate(appointment.scheduledDateTime),
          displayTime: this.formatTime(appointment.scheduledDateTime),
          displayServiceName: this.getServiceName(appointment)
        }));
        
        console.log('Dados processados:', processedData);
        this.appointments.set(processedData);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar agendamentos:', err);
        this.error.set('Erro ao carregar agendamentos. Por favor, tente novamente.');
        this.loading.set(false);
      }
    });
  }

  getUserName(appointment: any): string {
    // Tenta várias possibilidades de onde o nome do usuário pode estar
    if (appointment.userEmail) return appointment.userEmail;
    if (appointment.user?.name) return appointment.user.name;
    if (appointment.user?.email) return appointment.user.email;
    if (appointment.user?.username) return appointment.user.username;
    return `Usuário #${appointment.userId || appointment.user?.id || 'desconhecido'}`;
  }

  getServiceName(appointment: any): string {
    // Tenta várias possibilidades de onde o nome do serviço pode estar
    if (appointment.appointmentTypeName) return appointment.appointmentTypeName;
    if (appointment.appointmentType?.name) return appointment.appointmentType.name;
    if (appointment.appointmentTypeDescription) return appointment.appointmentTypeDescription;
    return 'Serviço não especificado';
  }

  formatDate(dateTimeStr: string): string {
    if (!dateTimeStr) return '';
    try {
      const date = new Date(dateTimeStr);
      return date.toLocaleDateString('pt-BR');
    } catch (e) {
      return dateTimeStr.split('T')[0] || '';
    }
  }

  formatTime(dateTimeStr: string): string {
    if (!dateTimeStr) return '';
    try {
      const date = new Date(dateTimeStr);
      return date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    } catch (e) {
      const timePart = dateTimeStr.split('T')[1];
      return timePart ? timePart.substring(0, 5) : '';
    }
  }

  editAppointment(appointment: any) {
    // Clonar o objeto para não modificar o original
    this.selectedAppointment.set({ ...appointment });
  }

  updateDateTime(event: Event) {
    const input = event.target as HTMLInputElement;
    const dateTime = input.value;
    
    if (!dateTime || !this.selectedAppointment()) return;
    
    const appointment = { ...this.selectedAppointment() };
    appointment.scheduledDateTime = dateTime;
    this.selectedAppointment.set(appointment);
  }

  saveChanges() {
    const appointment = this.selectedAppointment();
    if (!appointment) return;

    this.loading.set(true);
    
    // Extrair os IDs corretamente com base na estrutura dos dados
    const userId = appointment.userId;
    // Se não tiver um appointmentTypeId direto, tenta outras possibilidades
    const appointmentTypeId = appointment.appointmentTypeId || 
      (appointment.appointmentType?.id ? appointment.appointmentType.id : 
        // Como último recurso, tenta extrair da URL do serviço, se existir
        appointment.appointmentTypeAdress?.id);
    
    if (!userId || !appointmentTypeId) {
      this.error.set('Dados de usuário ou serviço incompletos');
      this.loading.set(false);
      return;
    }
    
    // Formatar a data para o formato esperado pelo backend
    let formattedDateTime = appointment.scheduledDateTime;
    if (formattedDateTime && !formattedDateTime.includes('T')) {
      // Se não tiver o 'T' do formato ISO, adiciona
      const [date, time] = formattedDateTime.split(' ');
      formattedDateTime = `${date}T${time || '00:00:00'}`;
    }
    
    console.log('Enviando data formatada:', formattedDateTime);
    
    const updateData: Partial<AppointmentSchedule> = {
      scheduledDateTime: formattedDateTime,
      user: { id: parseInt(userId) },
      appointmentType: { id: parseInt(appointmentTypeId) }
    };
    
    this.appointmentService.updateAppointment(appointment.id, updateData).subscribe({
      next: () => {
        this.fetchAppointments();
        this.selectedAppointment.set(null);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao salvar alterações:', err);
        this.error.set('Erro ao salvar alterações. Por favor, tente novamente.');
        this.loading.set(false);
      }
    });
  }

  deleteAppointment(id: number) {
    if (!confirm('Tem certeza que deseja excluir este agendamento?')) return;
    
    this.loading.set(true);
    
    this.appointmentService.deleteAppointment(id).subscribe({
      next: () => {
        this.fetchAppointments();
      },
      error: (err) => {
        console.error('Erro ao excluir agendamento:', err);
        this.error.set('Erro ao excluir agendamento. Por favor, tente novamente.');
        this.loading.set(false);
      }
    });
  }

  goBack() {
    this.location.back();
  }
}