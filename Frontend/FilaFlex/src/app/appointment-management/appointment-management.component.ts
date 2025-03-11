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
  isAdmin = signal<boolean>(false);
  currentUserId = signal<number | null>(null); 

  constructor(
    private appointmentService: AppointmentService,
    private location: Location
  ) {}

  ngOnInit() {
    this.debugLocalStorage();
    this.checkUserRole();
    this.fetchAppointments();
  }

  checkUserRole() {
    let isAdminUser = false;
    
    //Verifica o token JWT.
    const token = localStorage.getItem('token');
    if (token) {
      try {
        // Extrair e decodificar a parte de payload do token JWT
        const payload = token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payload));
        console.log('Token payload:', decodedPayload);
        
        // Verifica a role diretamente no token
        if (decodedPayload.role === 'ADMIN') {
          isAdminUser = true;
          console.log('Role ADMIN encontrada no token JWT');
        }
        
        // Armazena o ID do usuário para filtrar agendamentos
        if (decodedPayload.id) {
          this.currentUserId.set(decodedPayload.id);
        }
      } catch (e) {
        console.log('Erro ao decodificar token:', e);
      }
    }
    
    // Define o status de admin com base nas verificações
    this.isAdmin.set(isAdminUser);
    
    console.log('Resultado final da verificação: isAdmin =', isAdminUser);
  }

  debugLocalStorage() {
    console.log('==== DEBUG: Conteúdo do localStorage ====');
    
    //Listar todas as chaves do localStorage
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key) {
        try {
          const value = localStorage.getItem(key);
          console.log(`${key}:`, value);
          
          //Se o valor parece ser JSON, tenta parsear e mostrar a estrutura
          if (value && (value.startsWith('{') || value.startsWith('['))) {
            const parsedValue = JSON.parse(value);
            console.log(`${key} (parsed):`, parsedValue);
            
            //Se for o objeto de usuário, verifica suas propriedades
            if (key === 'user') {
              console.log('user.role:', parsedValue.role);
              console.log('user.roles:', parsedValue.roles);
              console.log('user.authorities:', parsedValue.authorities);
            }
          }
        } catch (e) {
          console.log(`Erro ao processar ${key}:`, e);
        }
      }
    }
    
    console.log('=======================================');
  }

  fetchAppointments() {
    this.loading.set(true);
    this.error.set(null);
    
    // Chamada diferente baseada na role do usuário
    if (this.isAdmin()) {
      // Admin vê todos os agendamentos
      this.appointmentService.getAppointments().subscribe({
        next: (data) => {
          console.log('Dados brutos recebidos (admin):', data);
          
          const processedData = data.map(appointment => ({
            ...appointment,
            displayUserName: this.getUserName(appointment),
            displayDate: this.formatDate(appointment.scheduledDateTime),
            displayTime: this.formatTime(appointment.scheduledDateTime),
            displayServiceName: this.getServiceName(appointment)
          }));
          
          console.log('Dados processados (admin):', processedData);
          this.appointments.set(processedData);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Erro ao buscar agendamentos:', err);
          this.error.set('Erro ao carregar agendamentos. Por favor, tente novamente.');
          this.loading.set(false);
        }
      });
    } else {
      // Usuário comum vê apenas seus agendamentos usando o novo endpoint
      const userId = this.currentUserId();
      if (!userId) {
        this.error.set('ID de usuário não encontrado');
        this.loading.set(false);
        return;
      }
      
      console.log('Buscando agendamentos para o usuário ID:', userId);
      
      // Usando o novo método do serviço
      this.appointmentService.getAppointmentsByUserId(userId).subscribe({
        next: (data) => {
          console.log('Dados brutos recebidos (usuário específico):', data);
          
          const processedData = data.map(appointment => ({
            ...appointment,
            displayUserName: this.getUserName(appointment),
            displayDate: this.formatDate(appointment.scheduledDateTime),
            displayTime: this.formatTime(appointment.scheduledDateTime),
            displayServiceName: this.getServiceName(appointment)
          }));
          
          console.log('Dados processados (usuário específico):', processedData);
          this.appointments.set(processedData);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Erro ao buscar agendamentos do usuário:', err);
          this.error.set('Erro ao carregar seus agendamentos. Por favor, tente novamente.');
          this.loading.set(false);
        }
      });
    }
  }

  getUserName(appointment: any): string {
    if (appointment.userEmail) return appointment.userEmail;
    if (appointment.user?.name) return appointment.user.name;
    if (appointment.user?.email) return appointment.user.email;
    if (appointment.user?.username) return appointment.user.username;
    return `Usuário #${appointment.userId || appointment.user?.id || 'desconhecido'}`;
  }

  getServiceName(appointment: any): string {
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