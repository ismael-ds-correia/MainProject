import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';

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

  constructor(
    private fb: FormBuilder,
    private appointmentTypeService: AppointmentTypeService
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
    this.appointmentTypeService.getAppointmentTypes().subscribe(
      data => {
        this.appointmentTypes = data;
        console.log('Appointment types carregados:', data);
      },
      error => {
        console.error('Erro ao carregar os tipos de serviço:', error);
      }
    );
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
      this.appointmentTypeService.updateAppointmentType(formData).subscribe(
        updated => {
          console.log('Serviço atualizado com sucesso:', updated);
          const index = this.appointmentTypes.findIndex(a => a.name === this.currentAppointmentType?.name);
          if (index !== -1) {
            this.appointmentTypes[index] = updated;
          }
          this.resetForm();
        },
        error => {
          console.error('Erro ao atualizar o serviço:', error);
        }
      );
    } else {
      // Adicionar novo serviço
      this.appointmentTypeService.createAppointmentType(formData).subscribe(
        created => {
          console.log('Serviço criado com sucesso:', created);
          this.appointmentTypes.push(created);
          this.resetForm();
        },
        error => {
          console.error('Erro ao criar o serviço:', error);
        }
      );
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
      address: {
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
      address: appointmentType.address || {}
    });
  }

  deleteAppointmentType(appointmentType: AppointmentType): void {
    if (confirm(`Tem certeza que deseja excluir o serviço "${appointmentType.name}"?`)) {
      // Agora passa o appointmentType completo, não apenas o ID
      this.appointmentTypeService.deleteAppointmentType(appointmentType).subscribe(
        () => {
          console.log('Serviço excluído com sucesso');
          this.appointmentTypes = this.appointmentTypes.filter(a => a.name !== appointmentType.name);
        },
        error => {
          console.error('Erro ao excluir o serviço:', error);
        }
      );
    }
  }

  cancelForm(): void {
    this.resetForm();
  }

  resetForm(): void {
    this.appointmentTypeForm.reset();
    this.isEditing = false;
    this.currentAppointmentType = null;
    this.showForm = false;
  }
}