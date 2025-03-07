import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';
import { CommonModule, CurrencyPipe, SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, SlicePipe, FormsModule],
  template: `
    <div class="home-container">
      <h1>Bem-vindo à página inicial</h1>
      <button (click)="logout()" class="logout-button">Logout</button>
      
      <!-- Botão para abrir o modal de seleção de categorias -->
      <button (click)="openCategoryModal()" class="search-button">Buscar por Categorias</button>

      <!-- Modal de seleção de categorias -->
      <div *ngIf="showCategoryModal" class="modal">
        <div class="modal-content">
          <span class="close" (click)="closeCategoryModal()">&times;</span>
          <h2 style="color: black;">Selecione Categorias</h2>
          
          <!-- Mensagem quando não há categorias -->
          <p *ngIf="allCategories.length === 0" style="color: black;">Carregando categorias...</p>
          
          <!-- Lista de categorias -->
          <div *ngFor="let category of allCategories" class="category-item">
            <label style="display: flex; align-items: center; color: black;">
              <input 
                type="checkbox" 
                [value]="category" 
                (change)="onCategoryChange($event)"
                style="margin-right: 8px;"
              >
              <span style="color: black; font-weight: normal;">{{ category }}</span>
            </label>
          </div>
          
          <!-- Debug info -->
          <p *ngIf="allCategories.length > 0" style="color: gray; font-size: 12px;">
            {{ allCategories.length }} categorias disponíveis
          </p>
          
          <button 
            (click)="searchByCategories()" 
            class="search-button"
            style="margin-top: 15px; padding: 8px 16px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;">
            Buscar
          </button>
        </div>
      </div>

      <div class="appointment-grid">
        <div *ngFor="let appointmentType of filteredAppointmentTypes" class="appointment-card">
          <h2>{{ appointmentType.name }}</h2>
          <p class="description">{{ appointmentType.description | slice:0:50 }}...</p>
          <p class="price">Preço: {{ appointmentType.price | currency:'BRL' }}</p>
          <p class="time">Tempo estimado: {{ appointmentType.estimatedTime }} minutos</p>
        </div>
      </div>
  `,
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  appointmentTypes: AppointmentType[] = [];
  filteredAppointmentTypes: AppointmentType[] = [];
  searchTerm: string = '';
  showCategoryModal: boolean = false;
  allCategories: string[] = [];
  selectedCategories: string[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private appointmentTypeService: AppointmentTypeService
  ) {}

  ngOnInit(): void {
    this.appointmentTypeService.getAppointmentTypes().subscribe((data) => {
      this.appointmentTypes = data;
      this.filteredAppointmentTypes = data;
    });

    this.appointmentTypeService.getAllCategories().subscribe((categories) => {
      this.allCategories = categories;
      console.log('Categorias extraídas:', this.allCategories);
    });
  }

  extractCategories(): void {
    const categoriesSet = new Set<string>();
    this.appointmentTypes.forEach((appointmentType) => {
      appointmentType.category.forEach((category) => {
        categoriesSet.add(category);
      });
    });

    this.allCategories = Array.from(categoriesSet);
    console.log('Categorias extraídas:', this.allCategories);
  }

  openCategoryModal(): void {
    this.showCategoryModal = true;
    console.log('Modal aberto');
  }

  closeCategoryModal(): void{
    this.showCategoryModal = false;
  }

  onCategoryChange(event: Event): void{
    const checkbox = event.target as HTMLInputElement;
    if(checkbox.checked){
      this.selectedCategories.push(checkbox.value);
    }else{
      this.selectedCategories = this.selectedCategories.filter(category => category !== checkbox.value);
    }
    console.log('Categorias selecionadas:', this.selectedCategories);
  }

  searchByCategories(): void {
    if (this.selectedCategories.length === 0) {
      this.filteredAppointmentTypes = [...this.appointmentTypes];
    } else {
      this.filteredAppointmentTypes = this.appointmentTypes.filter(appointmentType =>
        appointmentType.category.some(category => this.selectedCategories.includes(category))
      );
    }
    
    console.log('Appointments filtrados:', this.filteredAppointmentTypes);
    this.closeCategoryModal();
  }

  search(): void{
    if(this.searchTerm.trim() === ''){
      this.filteredAppointmentTypes = this.appointmentTypes;
    }else{
      this.filteredAppointmentTypes = this.appointmentTypes.filter((appointmentType) => {
        return appointmentType.name.toLowerCase().includes(this.searchTerm.toLowerCase());
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}