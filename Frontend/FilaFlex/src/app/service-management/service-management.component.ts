import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';
import { catchError, Observable } from 'rxjs';
import { MetricsService, MetricsDTO } from '../services/metrics.service';

@Component({
  selector: 'app-service-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './service-management.component.html',
  styleUrls: ['./service-management.component.css']
})
export class ServiceManagementComponent implements OnInit {
  appointmentTypes: AppointmentType[] = [];
  appointmentTypeForm!: FormGroup;
  showForm = false;
  isEditing = false;
  currentAppointmentType: AppointmentType | null = null;
  showReportModal = false;
  selectedServiceForReport: AppointmentType | null = null;
  metrics: MetricsDTO | null = null;
  loadingMetrics = false;
  reportStartDate: Date | null = null;
  reportEndDate: Date | null = null;
  metricsError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private appointmentTypeService: AppointmentTypeService,
    private metricsService: MetricsService 
  ) {}

  ngOnInit(): void {
    this.loadAppointmentTypes();
    this.initForm();
  }
  
  initForm(): void {
    this.appointmentTypeForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      category: [''],
      price: [0, [Validators.required, Validators.min(0.01)]],
      estimatedTime: [0, [Validators.required, Validators.min(1)]],
      appointmentDate: [''],
      requiredDocumentation: [''],
      address: this.fb.group({
        number: ['', Validators.required],  // Campo obrigatório
        street: ['', Validators.required],  // Campo obrigatório
        city: ['', Validators.required],    // Campo obrigatório
        state: ['', Validators.required],   // Campo obrigatório
        country: ['', Validators.required]  // Campo obrigatório
      })
    });
  }
  
  loadAppointmentTypes(): void {
    this.appointmentTypeService.getAppointmentTypes().subscribe({
      next: (data) => {
        this.appointmentTypes = data;
        console.log('AppointmentTypes carregados:', data);
      },
      error: (error) => {
        console.error('Erro ao carregar os AppointmentTypes:', error);
      }
    });
  }
  
  toggleForm(): void {
    this.showForm = !this.showForm;
    if (this.showForm && !this.isEditing) {
      this.appointmentTypeForm.reset();
    }
  }
  
  saveAppointmentType(): void {
    if (this.appointmentTypeForm.invalid) {
      return;
    }
  
    const formData = this.prepareFormData();
    console.log('Dados preparados para envio:', formData);
  
    if (this.isEditing && this.currentAppointmentType) {
      // Atualizar serviço existente
      this.appointmentTypeService.updateAppointmentType(formData).subscribe({
        next: (updated) => {
          console.log('Serviço atualizado com sucesso:', updated);
          const index = this.appointmentTypes.findIndex(a => a.name === this.currentAppointmentType?.name);
          if (index !== -1) {
            this.appointmentTypes[index] = updated;
          }
          this.resetForm();
        },
        error: (error) => {
          console.error('Erro ao atualizar o serviço:', error);
        }
      });
    } else {
      // Adicionar novo serviço
      this.appointmentTypeService.createAppointmentType(formData).subscribe({
        next: (created) => {
          console.log('Serviço criado com sucesso:', created);
          this.appointmentTypes.push(created);
          this.resetForm();
        },
        error: (error) => {
          console.error('Erro ao criar o serviço:', error);
        }
      });
    }
  }

  prepareFormData(): AppointmentType {
    const formValue = this.appointmentTypeForm.value;
    
    // Converter strings separadas por vírgula para arrays
    const category = formValue.category ? 
      formValue.category.split(',').map((c: string) => c.trim()).filter(Boolean) : 
      [];
    const requiredDocumentation = formValue.requiredDocumentation ? 
      formValue.requiredDocumentation.split(',').map((d: string) => d.trim()).filter(Boolean) : 
      [];

    // Construir um objeto que corresponda ao formato esperado pelo serviço
    return {
      name: formValue.name,
      description: formValue.description,
      category: category,
      price: formValue.price,
      estimatedTime: formValue.estimatedTime,
      appointmentDate: formValue.appointmentDate || '',
      requiredDocumentation: requiredDocumentation,
      adress: {
        number: formValue.address?.number || '',
        street: formValue.address?.street || '',
        city: formValue.address?.city || '',
        state: formValue.address?.state || '',
        country: formValue.address?.country || ''
      }
    };
  }

  editAppointmentType(appointmentType: AppointmentType): void {
    this.isEditing = true;
    this.currentAppointmentType = appointmentType;
    this.showForm = true;
  
    // Converter arrays para strings separadas por vírgula
    const categoryStr = appointmentType.category ? appointmentType.category.join(', ') : '';
    const docsStr = appointmentType.requiredDocumentation ? appointmentType.requiredDocumentation.join(', ') : '';
  
    this.appointmentTypeForm.patchValue({
      name: appointmentType.name,
      description: appointmentType.description,
      category: categoryStr,
      price: appointmentType.price,
      estimatedTime: appointmentType.estimatedTime,
      appointmentDate: appointmentType.appointmentDate,
      requiredDocumentation: docsStr,
      address: appointmentType.adress || {}
    });
  }


  deleteAppointmentType(appointmentType: AppointmentType): void {
    if (!appointmentType.name) {
      console.error('Não é possível excluir: nome não encontrado');
      return;
    }
    
    this.appointmentTypeService.deleteAppointmentTypeByName(appointmentType.name)
      .subscribe({
        next: () => {
          console.log('Serviço excluído com sucesso');
          this.appointmentTypes = this.appointmentTypes.filter(a => a.name !== appointmentType.name);
        },
        error: (error) => {
          console.error('Erro ao excluir o serviço:', error);
        }
      });
  }

  cancelForm(): void {
    this.resetForm();
  }

  openReportModal(appointmentType: AppointmentType): void {
    this.selectedServiceForReport = appointmentType;
    this.metrics = null;
    this.metricsError = null;
    this.showReportModal = true;
    this.reportEndDate = new Date();
    this.reportStartDate = new Date();
    this.reportStartDate.setMonth(this.reportStartDate.getMonth() - 1);
  }

  closeReportModal(): void {
    this.showReportModal = false;
    this.selectedServiceForReport = null;
    this.metrics = null;
  }

  generateReport(): void {
    if (!this.selectedServiceForReport) {
      return;
    }

    this.loadingMetrics = true;
    this.metricsError = null;
    
    this.metricsService.getMetricsByAppointmentType(
      this.selectedServiceForReport.name,
      this.reportStartDate || undefined,
      this.reportEndDate || undefined
    ).subscribe({
      next: (data) => {
        this.metrics = data;
        this.loadingMetrics = false;
      },
      error: (error) => {
        console.error('Erro ao carregar métricas:', error);
        this.loadingMetrics = false;
        if (error.status === 404) {
          this.metricsError = 'Nenhum dado de métricas encontrado para este serviço no período selecionado.';
        } else {
          this.metricsError = 'Erro ao carregar métricas. Tente novamente mais tarde.';
        }
      }
    });
  }

  resetForm(): void {
    this.appointmentTypeForm.reset();
    this.isEditing = false;
    this.currentAppointmentType = null;
    this.showForm = false;
  }
}