import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentService, AppointmentSchedule } from '../services/appointment.service';
import { AuthService } from '../auth/services/auth.service';
import { Location } from '@angular/common';  // Importado aqui

@Component({
  selector: 'app-appointment-scheduling',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private location: Location  // Adicionado aqui
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
        this.appointmentTypeId = +id;  // Converte para número
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
    // Formato ISO-8601: "2025-03-12T14:00:00"
    const date = formValues.date;
    const time = formValues.time || '00:00';
    
    // Garante que a data está no formato correto
    let dateTime = `${date}T${time}:00`;
    
    // Exibe o formato para verificação
    console.log('Data formatada para envio:', dateTime);
  
    const appointment = {
      appointmentType: {
        id: this.appointmentTypeId
      },
      user: {
        id: parseInt(userId)
      },
      scheduledDateTime: dateTime
    };
  
    console.log('Enviando agendamento com dados:', appointment);
    
    this.appointmentService.scheduleAppointment(appointment).subscribe({
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