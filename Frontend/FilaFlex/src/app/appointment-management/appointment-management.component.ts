import { Component, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Location } from '@angular/common';
import { NgIf, NgFor } from '@angular/common';
import { CommonModule } from '@angular/common';
import { AppointmentService, AppointmentSchedule } from '../services/appointment.service';
import { FormsModule } from '@angular/forms';
import { AppointmentTypeService, EvaluationDTO } from '../services/appointment-type.service';

@Component({
  selector: 'app-appointment-management',
  standalone: true,
  templateUrl: './appointment-management.component.html',
  styleUrls: ['./appointment-management.component.css'],
  imports: [NgIf, NgFor, CommonModule, FormsModule]
})
export class AppointmentManagementComponent implements OnInit {
  loading = signal(false);
  error = signal<string | null>(null);
  appointments = signal<any[]>([]);
  selectedAppointment = signal<any | null>(null);
  isAdmin = signal<boolean>(false);
  currentUserId = signal<number | null>(null); 
  allAppointments = signal<any[]>([]);
  users = signal<any[]>([]);
  evaluationModal = signal<{
    visible: boolean,
    appointmentTypeId: number | null,
    appointmentTypeName: string,
    rating: number,
    comment: string
  }>({
    visible: false,
    appointmentTypeId: null,
    appointmentTypeName: '',
    rating: 0,
    comment: ''
  });

  constructor(
    private appointmentService: AppointmentService,
    private appointmentTypeService: AppointmentTypeService,
    private location: Location,
    private http: HttpClient
  ) {}

  confirmDialog = signal<{visible: boolean, appointmentId: number | null}>({
    visible: false,
    appointmentId: null
  });

  ngOnInit() {
    this.debugLocalStorage();
    this.checkUserRole();
    this.fetchAppointments();

    if (this.isAdmin()) {
      this.fetchUsers();
    }
  }

  openEvaluationModal(appointment: any) {
    console.log('Dados do agendamento para avaliação:', appointment);
    
    // Obter o nome do serviço diretamente do appointment
    const serviceName = this.getServiceName(appointment);
    
    // Tentar obter o ID diretamente dos dados do agendamento - sem chamar a API
    let serviceId = null;
    
    if (appointment.appointmentTypeId) {
      serviceId = appointment.appointmentTypeId;
    } 
    else if (appointment.appointmentType?.id) {
      serviceId = appointment.appointmentType.id;
    }
    else if (appointment.service?.id) {
      serviceId = appointment.service.id;
    }
    else if (appointment.id) {
      // Como último recurso, usar o ID do próprio agendamento
      serviceId = appointment.id; 
    }
    
    // Abrir o modal imediatamente com os dados que temos
    this.evaluationModal.set({
      visible: true,
      appointmentTypeId: serviceId || 1, // Usar ID 1 como fallback se não encontramos nenhum ID
      appointmentTypeName: serviceName || 'Serviço',
      rating: 0,
      comment: ''
    });
    
    // Registrar no console para fins de depuração, mas não fechar o modal
    if (!serviceId) {
      console.warn('Aviso: ID do serviço não identificado. Usando ID padrão para avaliação.');
    }
  }

  closeEvaluationModal() {
    this.evaluationModal.set({
      visible: false,
      appointmentTypeId: null,
      appointmentTypeName: '',
      rating: 0,
      comment: ''
    });
  }
  
  setRating(rating: number) {
    const currentModal = this.evaluationModal();
    this.evaluationModal.set({
      ...currentModal,
      rating: rating
    });
  }
  
  getRatingText(): string {
    const rating = this.evaluationModal().rating;
    switch (rating) {
      case 1: return 'Muito ruim';
      case 2: return 'Ruim';
      case 3: return 'Regular';
      case 4: return 'Bom';
      case 5: return 'Excelente';
      default: return 'Selecione uma classificação';
    }
  }

  isValidRating(): boolean {
    return this.evaluationModal().rating > 0;
  }

  submitEvaluation() {
    if (!this.isValidRating()) {
      this.error.set('Por favor, selecione uma classificação para o serviço');
      return;
    }
    
    const { appointmentTypeId, rating, comment } = this.evaluationModal();
    
    // Garantir que temos um ID válido
    const idToUse = appointmentTypeId || 1; // Usar ID 1 como fallback
    
    const evaluation: EvaluationDTO = {
      rating,
      comment: comment?.trim() || '',
      appointmentTypeId: idToUse
    };
    
    this.loading.set(true);
    
    this.appointmentTypeService.submitEvaluation(evaluation).subscribe({
      next: (response) => {
        console.log('Avaliação enviada com sucesso:', response);
        this.showSuccessMessage('Avaliação enviada com sucesso!');
        this.closeEvaluationModal();
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao enviar avaliação:', err);
        this.error.set('Erro ao enviar avaliação. Por favor, tente novamente.');
        this.loading.set(false);
        // NÃO fechamos o modal em caso de erro
      }
    });
  }

  updateComment(event: Event) {
    const input = event.target as HTMLTextAreaElement;
    const comment = input.value;
    
    const currentModal = this.evaluationModal();
    this.evaluationModal.set({
      ...currentModal,
      comment
    });
  }

  registerCheckIn(appointmentId: number): void {
    if (!appointmentId) {
      this.error.set('ID do agendamento não encontrado');
      return;
    }
    
    this.loading.set(true);
    this.error.set(null);
    
    this.appointmentService.registerCheckIn(appointmentId).subscribe({
      next: (response) => {
        console.log('Check-in registrado com sucesso');
        this.showSuccessMessage('Check-in registrado com sucesso!');
        setTimeout(() => {
          this.fetchAppointments();
        }, 300);
      },
      error: (err) => {
        console.error('Erro ao registrar check-in:', err);
        if (err.status === 404) {
          this.error.set('Agendamento não encontrado');
        } else if (err.status === 400) {
          this.error.set('Não foi possível registrar o check-in. Verifique o status do agendamento.');
        } else {
          this.error.set('Erro ao registrar check-in. Por favor, tente novamente.');
        }
        this.loading.set(false);
      },
      complete: () => {
        this.loading.set(false);
      }
    });
  }

  checkUserRole() {
    let isAdminUser = false;
    
    //Verifica o token JWT.
    const token = localStorage.getItem('token');
    if (token) {
      try {
        //Extrair e decodificar a parte de payload do token JWT
        const payload = token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payload));
        console.log('Token payload:', decodedPayload);
        
        //Verifica a role diretamente no token
        if (decodedPayload.role === 'ADMIN') {
          isAdminUser = true;
          console.log('Role ADMIN encontrada no token JWT');
        }
        
        //Armazena o ID do usuário para filtrar agendamentos
        if (decodedPayload.id) {
          this.currentUserId.set(decodedPayload.id);
        }
      } catch (e) {
        console.log('Erro ao decodificar token:', e);
      }
    }
    
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
    
    //Chamada diferente baseada na role do usuário
    if (this.isAdmin()) {
      //Admin vê todos os agendamentos
      this.appointmentService.getAppointments().subscribe({
        next: (data) => {
          console.log('Dados brutos recebidos (admin):', data);
          
          const processedData = data.map(appointment => ({
            ...appointment,
            displayUserName: this.getUserName(appointment),
            displayDate: this.formatDate(appointment.scheduledDateTime),
            displayTime: this.formatTime(appointment.scheduledDateTime),
            displayServiceName: this.getServiceName(appointment),
            displayAddress: this.getAddress(appointment)
          }));
          
          console.log('Dados processados (admin):', processedData);
          this.appointments.set(processedData);
          this.allAppointments.set(processedData);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Erro ao buscar agendamentos:', err);
          this.error.set('Erro ao carregar agendamentos. Por favor, tente novamente.');
          this.loading.set(false);
        }
      });
    } else {
      //Usuário comum vê apenas seus agendamentos usando o novo endpoint
      const userId = this.currentUserId();
      if (!userId) {
        this.error.set('ID de usuário não encontrado');
        this.loading.set(false);
        return;
      }
      
      console.log('Buscando agendamentos para o usuário ID:', userId);
      
      this.appointmentService.getAppointmentsByUserId(userId).subscribe({
        next: (data) => {
          console.log('Dados brutos recebidos (usuário específico):', data);
          
          const processedData = data.map(appointment => ({
            ...appointment,
            displayUserName: this.getUserName(appointment),
            displayDate: this.formatDate(appointment.scheduledDateTime),
            displayTime: this.formatTime(appointment.scheduledDateTime),
            displayServiceName: this.getServiceName(appointment),
            displayAddress: this.getAddress(appointment)
          }));
          
          console.log('Dados processados (usuário específico):', processedData);
          this.appointments.set(processedData);
          this.allAppointments.set(processedData);
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

  //Método para buscar usuários
  fetchUsers() {
    this.appointmentService.getUsers().subscribe({
      next: (data) => {
        console.log('Usuários carregados:', data);
        this.users.set(data);
      },
      error: (error) => {
        console.error('Erro ao carregar usuários:', error);
      }
    });
  }

  //Método para filtrar por usuário selecionado
  filterByUser() {
    const userSelect = document.getElementById('userSearch') as HTMLSelectElement;
    const userId = userSelect.value;
    
    if (!userId) {
      //Se nenhum usuário estiver selecionado, mostra todos os agendamentos
      this.appointments.set(this.allAppointments());
      return;
    }
    
    console.log('Filtrando por usuário ID:', userId);
    
    // Filtra os agendamentos pelo usuário selecionado
    const filteredAppointments = this.allAppointments().filter(appointment => {
      const appointmentUserId = 
        appointment.userId || 
        (appointment.user ? appointment.user.id : null);
      
      return appointmentUserId == userId; //
    });
    
    console.log('Agendamentos filtrados:', filteredAppointments);
    this.appointments.set(filteredAppointments);
  }

  //Método para filtrar por intervalo de datas
  filterByDate() {
    const startDateInput = document.getElementById('startDate') as HTMLInputElement;
    const endDateInput = document.getElementById('endDate') as HTMLInputElement;
    
    const startDate = startDateInput.value ? new Date(startDateInput.value) : null;
    const endDate = endDateInput.value ? new Date(endDateInput.value) : null;
    
    if (!startDate && !endDate) {
      this.appointments.set(this.allAppointments());
      return;
    }
    
    console.log('Filtrando por intervalo de data:', startDate, endDate);
    
    // Filtra os agendamentos pelo intervalo de datas
    const filteredAppointments = this.allAppointments().filter(appointment => {
      const appointmentDate = new Date(appointment.scheduledDateTime);
      
      if (startDate && endDate) {
        return appointmentDate >= startDate && appointmentDate <= endDate;
      } else if (startDate) {
        return appointmentDate >= startDate;
      } else if (endDate) {
        return appointmentDate <= endDate;
      }
      
      return true; // Não deve chegar aqui, mas por segurança
    });
    
    console.log('Agendamentos filtrados por data:', filteredAppointments);
    this.appointments.set(filteredAppointments);
  }

  // Método para limpar todos os filtros
  clearFilters() {
    // Limpar os campos de input
    const startDateInput = document.getElementById('startDate') as HTMLInputElement;
    const endDateInput = document.getElementById('endDate') as HTMLInputElement;
    const userSelect = document.getElementById('userSearch') as HTMLSelectElement;
    
    if (startDateInput) startDateInput.value = '';
    if (endDateInput) endDateInput.value = '';
    if (userSelect) userSelect.value = '';
    
    // Restaurar todos os agendamentos
    this.appointments.set(this.allAppointments());
  }

  getAddress(appointment: any): string {
    // Verificar se o campo appointmentTypeAdress existe no objeto raiz
    if (appointment.appointmentTypeAdress) {
      const adress = appointment.appointmentTypeAdress;
      return `${adress.street}, ${adress.number} - ${adress.city}, ${adress.state}/${adress.country}`;
    }
    
    //Verificação alternativa se o endereço estiver aninhado no appointmentType
    if (appointment.appointmentType?.adress) {
      const adress = appointment.appointmentType.adress;
      return `${adress.street}, ${adress.number} - ${adress.city}, ${adress.state}/${adress.country}`;
    }
    
    //Mais uma verificação para address com dois 'd's
    if (appointment.appointmentType?.address) {
      const adress = appointment.appointmentType.address;
      return `${adress.street}, ${adress.number} - ${adress.city}, ${adress.state}/${adress.country}`;
    }
    
    return 'Endereço não disponível';
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
    
    const userId = appointment.userId;
    const appointmentTypeId = appointment.appointmentTypeId || 
      (appointment.appointmentType?.id ? appointment.appointmentType.id : 
        appointment.appointmentTypeAdress?.id);
    
    if (!userId || !appointmentTypeId) {
      this.error.set('Dados de usuário ou serviço incompletos');
      this.loading.set(false);
      return;
    }
    
    //Formatar a data para o formato esperado pelo backend
    let formattedDateTime = appointment.scheduledDateTime;
    if (formattedDateTime && !formattedDateTime.includes('T')) {
      //Se não tiver o 'T' do formato ISO, adiciona
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
    this.confirmDialog.set({
      visible: true,
      appointmentId: id
    });
  }

  cancelDelete() {
    this.confirmDialog.set({
      visible: false,
      appointmentId: null
    });
  }

  confirmDelete() {
    const id = this.confirmDialog().appointmentId;
    if (!id) return;
    
    this.confirmDialog.set({
      visible: false,
      appointmentId: null
    });
    
    this.loading.set(true);
    this.error.set(null);
    
    this.appointmentService.deleteAppointment(id).subscribe({
      next: (response) => {
        console.log('Exclusão processada com sucesso');
        this.showSuccessMessage('Agendamento excluído com sucesso!');
        
        setTimeout(() => {
          this.fetchAppointments();
        }, 300);
      },
      error: (err) => {
        if (err.status === 200) {
          console.log('Status 200 tratado como sucesso no componente');
          this.showSuccessMessage('Agendamento excluído com sucesso!');
          setTimeout(() => {
            this.fetchAppointments();
          }, 300);
          return;
        }
        
        console.error('Erro ao excluir agendamento:', err);
        this.error.set('Erro ao excluir agendamento. Por favor, tente novamente.');
        this.loading.set(false);
      },
      complete: () => {
        console.log('Operação de exclusão completa');
      }
    });
  }

  //Método para exibir mensagem de sucesso após apagar.
  private showSuccessMessage(message: string): void {
    const messageElement = document.createElement('div');
    messageElement.className = 'success-message';
    messageElement.textContent = message;
    
    const container = document.querySelector('.appointment-container');
    if (container) {
      container.appendChild(messageElement);
      
      setTimeout(() => {
        try {
          container.removeChild(messageElement);
        } catch (e) {
          console.log('Elemento de mensagem já removido');
        }
      }, 3000);
    }
  }

  goBack() {
    this.location.back();
  }
}