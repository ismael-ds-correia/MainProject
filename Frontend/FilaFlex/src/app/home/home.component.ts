import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/services/auth.service';
import { AppointmentType, AppointmentTypeService } from '../services/appointment-type.service';
import { CommonModule, CurrencyPipe, SlicePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { environment } from '../../environments/environment';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, SlicePipe, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  appointmentTypes: AppointmentType[] = [];
  filteredAppointmentTypes: AppointmentType[] = [];
  searchTerm: string = '';
  showCategoryModal: boolean = false;
  allCategories: string[] = [];
  selectedCategories: string[] = [];
  showTimeModal: boolean = false;
  minTime: number = 0;
  maxTime: number = 180; 
  
  //Propriedades para busca por preço
  minPrice: number = 0;
  maxPrice: number = 1000;
  showPriceModal: boolean = false;

  //Permite flexibilidade para mudar a URL da API
  apiUrl: string = environment.apiUrl;
  isProduction: boolean = environment.production;

  constructor(
    private authService: AuthService,
    private router: Router,
    private appointmentTypeService: AppointmentTypeService
  ) {
    console.log('Ambiente:', environment.production ? 'Produção' : 'Desenvolvimento');
    console.log('API URL:', environment.apiUrl);
  }

  ngOnInit(): void {
    console.log('Environment API URL:', this.apiUrl);
    console.log('Is Production:', this.isProduction);

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
  
  //Método para busca por intervalo de preço
  openPriceModal(): void {
    this.showPriceModal = true;
    console.log('Modal de preço aberto');
  }

  closePriceModal(): void {
    this.showPriceModal = false;
  }

  searchByPriceRange(): void {
    if (this.minPrice < 0) this.minPrice = 0;
    if (this.maxPrice < this.minPrice) this.maxPrice = this.minPrice;
    
    this.appointmentTypeService.findByPriceRange(this.minPrice, this.maxPrice)
      .subscribe({
        next: (results) => {
          this.filteredAppointmentTypes = results;
          console.log(`Encontrados ${results.length} serviços no intervalo de preço R$${this.minPrice} a R$${this.maxPrice}`);
          this.closePriceModal();
        },
        error: (error) => {
          console.error('Erro ao buscar por intervalo de preço:', error);
          this.closePriceModal();
        }
      });
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

  //Método para abrir o modal de tempo estimado
  openTimeModal(): void {
    this.showTimeModal = true;
    console.log('Modal de tempo estimado aberto');
  }

  //Método para fechar o modal de tempo estimado
  closeTimeModal(): void {
    this.showTimeModal = false;
  }

  //Método para buscar por intervalo de tempo estimado
  searchByTimeRange(): void {
    if (this.minTime < 0) this.minTime = 0;
    if (this.maxTime < this.minTime) this.maxTime = this.minTime;
    
    this.filteredAppointmentTypes = this.appointmentTypes.filter(appointmentType => {
      let estimatedTimeMinutes: number;
      
      if (typeof appointmentType.estimatedTime === 'string') {
        estimatedTimeMinutes = parseInt(appointmentType.estimatedTime);
      } else if (typeof appointmentType.estimatedTime === 'number') {
        estimatedTimeMinutes = appointmentType.estimatedTime;
      } else {
        estimatedTimeMinutes = 0;
        console.warn('Tipo de tempo estimado inválido:', appointmentType);
      }
      
      return !isNaN(estimatedTimeMinutes) && 
            estimatedTimeMinutes >= this.minTime && 
            estimatedTimeMinutes <= this.maxTime;
    });
    
    console.log(`Encontrados ${this.filteredAppointmentTypes.length} serviços no intervalo de ${this.minTime} a ${this.maxTime} minutos`);
    this.closeTimeModal();
  }

  isAdmin(): boolean {
    const userRole = localStorage.getItem('userRole');
    console.log('User-role no localStorage:', userRole);
    return userRole === 'ADMIN';
  }
  
  navigateToServiceManagement(): void {
    this.router.navigate(['/service-management']);
  }
  
  navigateToAppointmentManagement(): void {
    this.router.navigate(['/appointment-management']);
  }

  viewDetails(name: string): void {
    this.router.navigate(['/appointment-details', name]);
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}