import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentService, AppointmentSchedule } from '../services/appointment.service';
import { AuthService } from '../auth/services/auth.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-appointment-scheduling',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './appointment-scheduling.component.html',
  styleUrls: ['./appointment-scheduling.component.css']
})
export class AppointmentSchedulingComponent implements OnInit {
  appointmentForm: FormGroup;
  appointmentTypeId: number | null = null;
  loading = false;
  error: string | null = null;
  success = false;
  minDate: string = '';
  showPriorityModal: boolean = false;
  selectedPriorityCondition: string = 'NO_PRIORITY';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private location: Location
  ) {
    this.appointmentForm = this.formBuilder.group({
      date: ['', Validators.required],
      time: ['', Validators.required]
    });

    // Definir data mínima como hoje
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      console.log('Query params recebidos:', params);
      const id = params['id'];
      if (id) {
        this.appointmentTypeId = +id;
        console.log('ID do appointmentType capturado:', this.appointmentTypeId);
      } else {
        this.error = 'ID do serviço não especificado';
        console.error('ID não encontrado nos query params');
      }
    });
  }

  onSubmit(): void {
    if (this.appointmentForm.invalid || !this.appointmentTypeId) {
      console.error('Formulário inválido ou ID não disponível:', this.appointmentTypeId);
      return;
    }
  
    // Mostrar modal de prioridade em vez de prosseguir diretamente
    this.showPriorityModal = true;
  }

  cancelPrioritySelection(): void {
    this.showPriorityModal = false;
  }

  confirmPrioritySelection(): void {
    //Fechar o modal
    this.showPriorityModal = false;
    
    // Continuar com o processo de agendamento incluindo a prioridade selecionada
    this.proceedWithAppointment();
  }

  proceedWithAppointment(): void {
    this.loading = true;
    this.error = null;
    this.success = false;
  
    const userId = this.authService.getUserId();
    if (!userId) {
      this.error = 'Usuário não autenticado';
      this.loading = false;
      return;
    }
  
    const formValues = this.appointmentForm.value;
    
    // Formatação da data compatível com Java LocalDateTime
    const date = formValues.date;
    const time = formValues.time || '00:00';
    
    let dateTime = `${date}T${time}:00`;
    
    console.log('Data formatada para envio:', dateTime);
  
    // SOLUÇÃO: Remover a tipagem explícita e deixar o TypeScript inferir o tipo
    const appointment = {
      appointmentType: {
        id: this.appointmentTypeId!
      },
      user: {
        id: parseInt(userId)
      },
      scheduledDateTime: dateTime,
      priorityCondition: this.selectedPriorityCondition
    };
  
    console.log('Enviando agendamento com dados:', appointment);
    
    // Chamada para o serviço
    this.appointmentService.scheduleAppointment(appointment as any).subscribe({
      next: (response) => {
        console.log('Resposta do agendamento:', response);
        this.success = true;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro detalhado:', err);

        if (err.status === 400) {
          this.error = 'Erro no formato dos dados. Verifique se a data está correta.';
        } else {
          this.error = 'Erro ao agendar serviço. Por favor, tente novamente.';
        }
        
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.location.back();
  }
}