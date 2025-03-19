import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { catchError, map, Observable, of, switchMap, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AppointmentType {
  id?: number;
  name: string;
  description: string;
  category: string[];
  price: number;
  estimatedTime: number;
  appointmentDate: string;
  requiredDocumentation: string[];
  adress: {
    number: string;
    street: string;
    city: string;
    state: string;
    country: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AppointmentTypeService {
  private apiUrl: string; 
  private categoryUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = `${environment.apiUrl}/appointment-types`;
    this.categoryUrl = `${environment.apiUrl}/category`;

    //logs para debug
    console.log('AppointmentType usando environment:');
    console.log('- API URL:', this.apiUrl);
    console.log('- Ambiente:', environment.production ? 'Produção' : 'Desenvolvimento');
  }

  getAppointmentTypes(): Observable<AppointmentType[]> {
    return this.http.get<AppointmentType[]>(`${this.apiUrl}/all`).pipe(
      catchError(this.handleError)
    );
  }

  getAppointmentTypeByName(name: string): Observable<AppointmentType> {
    console.log(`Buscando serviço com nome: ${name}`);
    console.log(`URL completa: ${this.apiUrl}/name/${name}`);
    
    const headers = { 
      'Authorization': `Bearer ${localStorage.getItem('token')}` 
    };
    
    return this.http.get<AppointmentType>(`${this.apiUrl}/name/${name}`, { headers })
      .pipe(
        tap(data => {
          console.log('Dados recebidos:', data);
          console.log('ID recebido:', data.id ? `${data.id} (${typeof data.id})` : 'ID não encontrado');
        }),
        catchError(error => {
          console.error('Erro na requisição:', error);
          return throwError(() => error);
        })
      );
  }

  createAppointmentType(appointmentType: AppointmentType): Observable<AppointmentType> {
    console.log('Criando appointment type:', appointmentType);
    const appointmentTypeDTO = this.convertToDTO(appointmentType);
    
    //Adiciona o header Authorization com o token.
    const headers = { 
      'Authorization': `Bearer ${localStorage.getItem('token')}` 
    };
    
    return this.http.post<AppointmentType>(`${this.apiUrl}/create`, appointmentTypeDTO, { headers }).pipe(
      tap(response => console.log('Resposta da criação:', response)),
      catchError(this.handleError)
    );
  }

  getAppointmentTypeId(appointmentType: AppointmentType | null): number | null {
    if (!appointmentType) {
      console.error('AppointmentType é nulo');
      return null;
    }
    
    if (appointmentType.id === undefined || appointmentType.id === null) {
      console.error('ID não encontrado no appointmentType:', appointmentType);
      return null;
    }
    
    // Garantir que o ID é um número
    const id = Number(appointmentType.id);
    if (isNaN(id)) {
      console.error('ID não é um número válido:', appointmentType.id);
      return null;
    }
    
    console.log('ID válido encontrado:', id);
    return id;
  }

  updateAppointmentType(appointmentType: AppointmentType): Observable<AppointmentType> {
    console.log('Atualizando appointment type:', appointmentType);

    //Verifica se tem ID antes de tentar excluir
    if (!appointmentType.name) {
      console.error('Não é possível atualizar: nome não encontrado');
      return throwError(() => new Error('Nome não encontrado'));
    }

    return this.deleteAppointmentTypeByName(appointmentType.name)
      .pipe(
        tap(() => console.log('Appointment type excluído antes de atualizar')),
        switchMap(() => this.createAppointmentType(appointmentType)),
        catchError(this.handleError)
      );
  }

  private convertToDTO(appointmentType: AppointmentType): any {
    //Converter a data, se existir.
    let appointmentDate = null;
    if (appointmentType.appointmentDate) {
      appointmentDate = appointmentType.appointmentDate;
    }
    

    let adress = null;
    if (appointmentType.adress) {
      adress = {
        number: appointmentType.adress.number || '',
        street: appointmentType.adress.street || '',
        city: appointmentType.adress.city || '',
        state: appointmentType.adress.state || '',
        country: appointmentType.adress.country || ''
      };
    }
    
    return {
      name: appointmentType.name,
      description: appointmentType.description,
      category: appointmentType.category || [],
      price: appointmentType.price || 0,
      estimatedTime: appointmentType.estimatedTime || 0,
      appointmentDate: appointmentDate,
      requiredDocumentation: appointmentType.requiredDocumentation || [],
      adress: adress
    };
  }
  
  deleteAppointmentTypeById(id: number): Observable<void> {
    const headers = { 
      'Authorization': `Bearer ${localStorage.getItem('token')}` 
    };
    
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  deleteAppointmentType(appointmentType: AppointmentType): Observable<void> {
    if (appointmentType.id) {
      const headers = { 
        'Authorization': `Bearer ${localStorage.getItem('token')}` 
      };
      
      return this.http.delete<void>(`${this.apiUrl}/${appointmentType.id}`, { headers }).pipe(
        catchError(this.handleError)
      );
    } 

    else if (appointmentType.name) {
      return this.deleteAppointmentTypeByName(appointmentType.name);
    } 
    else {
      console.error('Não é possível excluir: nem ID nem nome foram encontrados');
      return throwError(() => new Error('ID ou nome não encontrado'));
    }
  }

  deleteAppointmentTypeByName(name: string): Observable<void> {
    const headers = { 
      'Authorization': `Bearer ${localStorage.getItem('token')}` 
    };

    return this.http.delete<void>(`${this.apiUrl}/delete/${name}`, {headers}).pipe(
      tap(data => console.log('Dados recebidos:', data))
    );
  }
  
  getAllCategories(): Observable<string[]> {
    return this.http.get<any[]>(`${this.categoryUrl}/all`).pipe(
      tap(response => console.log('Resposta bruta das categorias:', response)),
      map(categories => {
        return categories.map(category => category.name);
      }),
      tap(names => console.log('Nomes das categorias extraídos:', names)),
      catchError(this.handleError)
    );
  }

  //Método para buscar appointment types por intervalo de preço
  findByPriceRange(minPrice: number, maxPrice: number): Observable<AppointmentType[]> {
    const params = new HttpParams()
      .set('minPrice', minPrice.toString())
      .set('maxPrice', maxPrice.toString());

    return this.http.get<AppointmentType[]>(`${this.apiUrl}/price-range`, { params }).pipe(
      tap(response => console.log(`Encontrados ${response.length} serviços no intervalo de preço R$${minPrice} a R$${maxPrice}`)),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Ocorreu um erro na API:', error);
    return throwError(() => error);
  }
}