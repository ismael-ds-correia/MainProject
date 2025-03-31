import { Component, OnInit } from '@angular/core';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { QueueService, AppointmentResponse } from '../services/queue.service';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-appointment-type-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appointment-type-details.component.html',
  styleUrl: './appointment-type-details.component.css'
})

export class AppointmentTypeDetailsComponent implements OnInit {
  appointmentType: AppointmentType | null = null;
  loading: boolean = true;
  error: string | null = null;
  showQueue: boolean = false;
  queue: AppointmentResponse[] = [];
  loadingQueue: boolean = false;
  queueError: string | null = null;
  showRepositionDialog = false;
  selectedAppointment: AppointmentResponse | null = null;
  newPosition: number = 0;
  showNextAppointmentDialog = false;
  loadingNextAppointment = false;
  nextAppointment: AppointmentResponse | null = null;
  showEvaluations: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private appointmentTypeService: AppointmentTypeService,
    private queueService: QueueService,
    private authService: AuthService
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
          if (data.evaluations && !Array.isArray(data.evaluations)) {
            data.evaluations = [data.evaluations];
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

  repositionAppointment(): void {
    if (!this.selectedAppointment || !this.newPosition) {
      return;
    }
    
    this.loadingQueue = true;
    
    this.queueService.reorderQueue(this.selectedAppointment.id, this.newPosition)
      .subscribe({
        next: (response) => {
          console.log('Agendamento reposicionado com sucesso:', response);
          //Recarrega a fila para mostrar a nova ordem.
          this.loadQueue();
          this.showRepositionDialog = false;
          this.selectedAppointment = null;
        },
        error: (error) => {
          console.error('Erro ao reposicionar agendamento:', error);
          this.queueError = 'Erro ao reposicionar agendamento. Tente novamente.';
          this.loadingQueue = false;
          this.showRepositionDialog = false;
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }
  
  scheduleAppointment(): void {
    if (!this.appointmentType) {
      console.error('appointmentType é nulo');
      return;
    }
    
    const appointmentTypeId = this.appointmentTypeService.getAppointmentTypeId(this.appointmentType);
    
    if (appointmentTypeId) {
      console.log('Navegando para agendamento com ID:', appointmentTypeId);
      this.router.navigate(['/appointment-scheduling'], {
        queryParams: {
          id: appointmentTypeId,
          name: this.appointmentType.name
        }
      });
    } else {
      console.error('ID inválido, usando apenas o nome');
      this.router.navigate(['/appointment-scheduling'], {
        queryParams: { name: this.appointmentType.name }
      });
    }
  }

  toggleEvaluationsView(): void {
    this.showEvaluations = !this.showEvaluations;
  }

  isAdmin(): boolean {
    const userRole = localStorage.getItem('userRole');
    console.log('userRole:', userRole);
    
    return userRole === 'ADMIN';
  }

  //Alternar visibilidade da fila e carregar dados quando necessário.
  toggleQueueView(): void {
    this.showQueue = !this.showQueue;
    
    if (this.showQueue && (!this.queue || this.queue.length === 0)) {
      this.loadQueue();
    }
  }

  //Carregar a fila usando o serviço.
  loadQueue(): void {
    if (!this.appointmentType || !this.appointmentType.name) {
      this.queueError = "Nome do serviço não disponível";
      return;
    }
    
    this.loadingQueue = true;
    this.queueError = null;
    
    this.queueService.getQueueByAppointmentType(this.appointmentType.name)
      .subscribe({
        next: (response) => {
          this.queue = response;
          this.loadingQueue = false;
          
          // Se tivermos um agendamento em atendimento na fila
          const attendingAppointment = this.queueService.findAttendingAppointment(response);
          if (attendingAppointment && !this.showNextAppointmentDialog) {
            this.nextAppointment = attendingAppointment;
          }
        },
        error: (error) => {
          console.error('Erro ao carregar fila:', error);
          this.queueError = "Não foi possível carregar a fila de agendamentos.";
          this.loadingQueue = false;
        }
      });
  }

  openRepositionDialog(appointment: AppointmentResponse): void {
    this.selectedAppointment = appointment;
    this.newPosition = appointment.queueOrder || 1;
    this.showRepositionDialog = true;
  }
  
  cancelRepositioning(): void {
    this.showRepositionDialog = false;
    this.selectedAppointment = null;
  }

  //Método para chamar próximo da fila.
  callNextInQueue(): void {
    if (!this.appointmentType) {
      alert('Dados do tipo de agendamento não disponíveis');
      return;
    }
  
    this.loadingNextAppointment = true;
    this.showNextAppointmentDialog = true;
    this.nextAppointment = null;
    
    const appointmentTypeId = this.appointmentTypeService.getAppointmentTypeId(this.appointmentType);
    const appointmentTypeName = this.appointmentType.name;
    
    if (!appointmentTypeId || !appointmentTypeName) {
      this.loadingNextAppointment = false;
      alert('Não foi possível determinar o ID ou nome do serviço');
      return;
    }
    
    this.queueService.callNextAppointment(appointmentTypeId, appointmentTypeName)
      .subscribe({
        next: (response) => {
          this.nextAppointment = response;
          this.loadingNextAppointment = false;
          // Se for um agendamento em atendimento, abrir o modal
          this.showNextAppointmentDialog = true;
          // Recarregar a fila para mostrar as alterações
          this.loadQueue();
        },
        error: (error) => {
          console.error('Erro ao chamar próximo agendamento:', error);
          this.loadingNextAppointment = false;
          
          if (error.status === 404) {
            // Não há mais agendamentos na fila
            this.nextAppointment = null;
          } else {
            alert('Erro ao chamar próximo agendamento. Tente novamente.');
            this.closeNextAppointmentDialog();
          }
        }
      });
  }

  closeNextAppointmentDialog(): void {
    this.showNextAppointmentDialog = false;
  }

  //Método para concluir o agendamento atual.
  completeCurrentAppointment(): void {
    if (!this.nextAppointment || !this.nextAppointment.id) {
      return;
    }
    
    this.loadingNextAppointment = true;
    
    this.queueService.completeAppointment(this.nextAppointment.id)
      .subscribe({
        next: (response) => {
          this.loadingNextAppointment = false;
          this.closeNextAppointmentDialog();
          // Recarregar a fila para mostrar as alterações
          this.loadQueue();
          alert('Agendamento concluído com sucesso!');
        },
        error: (error) => {
          console.error('Erro ao concluir agendamento:', error);
          this.loadingNextAppointment = false;
          alert('Erro ao concluir agendamento. Tente novamente.');
        }
      });
  }
  //Método para marcar como ausente.
  markAsAbsent(): void {
    if (!this.nextAppointment || !this.nextAppointment.id) {
      return;
    }
    
    this.loadingNextAppointment = true;
    
    this.queueService.markAsAbsent(this.nextAppointment.id)
      .subscribe({
        next: (response) => {
          this.loadingNextAppointment = false;
          this.closeNextAppointmentDialog();
          // Recarregar a fila para mostrar as alterações
          this.loadQueue();
          alert('Agendamento marcado como ausente com sucesso!');
        },
        error: (error) => {
          console.error('Erro ao marcar agendamento como ausente:', error);
          this.loadingNextAppointment = false;
          alert('Erro ao marcar agendamento como ausente. Tente novamente.');
        }
      });
  }

  //Método auxiliar para obter rótulo de status em português.
  getStatusLabel(status: string): string {
    switch (status) {
      case 'MARKED': return 'Agendado';
      case 'ATTENDING': return 'Em atendimento';
      case 'COMPLETED': return 'Concluído';
      case 'ABSENT': return 'Ausente';
      case 'WAITING': return 'Aguardando';
      default: return status;
    }
  }

  get hasAttendingAppointment(): boolean {
    return this.queue.some(appointment => appointment.status === 'ATTENDING');
  }
}