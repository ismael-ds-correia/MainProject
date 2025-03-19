import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

//Interface para o que é recebido da API
export interface AppointmentResponse {
  id: number;
  scheduledDateTime: string;
  createdDateTime: string;
  userId: string;
  userEmail: string;
  appointmentTypeName: string;
  appointmentTypeDescription: string;
  appointmentTypeEstimatedTime: string;
  appointmentTypePrice: string;
  appointmentTypeCategory: string[];
  appointmentTypeAdress?: {
    id: number;
    number: string;
    street: string;
    city: string;
    state: string;
    country: string;
  };
}

// Interface atualizada para refletir tanto o que é usado pelo componente quanto o que vem da API
export interface AppointmentSchedule {
  id?: number;
  appointmentType: { id: number, name?: string };
  user: { id: number, name?: string, email?: string, username?: string };
  scheduledDateTime: string;
  
  userId?: number;
  userEmail?: string;
  appointmentTypeName?: string;
  appointmentTypeDescription?: string;
  appointmentTypeEstimatedTime?: string;
  appointmentTypePrice?: string;
  appointmentTypeCategory?: string[];
  appointmentTypeAdress?: {
    id: number;
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
export class AppointmentService {
  private apiUrl: string;
  private userApiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = `${environment.apiUrl}/appointment`;
    this.userApiUrl = `${environment.apiUrl}/user`;

    //logs para verificar se a URL da API está correta
    console.log('Appointment usando enviroment:');
    console.log('- API URL:', this.apiUrl);
    console.log('- Ambiente:', environment.production ? 'Produção' : 'Desenvolvimento');
   }

  /** Agendar novo compromisso */
  scheduleAppointment(appointment: AppointmentSchedule): Observable<any> {
    const headers = this.getHeaders();
    return this.http.post(`${this.apiUrl}/create`, appointment, { headers })
      .pipe(catchError(this.handleError));
  }

  /** Buscar todos os agendamentos */
  getAppointments(): Observable<AppointmentSchedule[]> {
    const headers = this.getHeaders();
    return this.http.get<AppointmentSchedule[]>(`${this.apiUrl}/all`, { headers })
      .pipe(catchError(this.handleError));
  }

  /** Método para buscar agendamentos por ID de usuário */
  getAppointmentsByUserId(userId: number): Observable<AppointmentSchedule[]> {
    const headers = this.getHeaders();
    // Criando parâmetros de consulta
    const params = new HttpParams().set('userId', userId.toString());
    
    // Fazendo a chamada HTTP com os parâmetros - espera resposta completa
    return this.http.get<AppointmentSchedule[]>(`${this.apiUrl}/user`, { 
      headers, 
      params 
    }).pipe(catchError(this.handleError));
  }

  /** Atualizar um agendamento */
  updateAppointment(id: number, updatedData: Partial<AppointmentSchedule>): Observable<any> {
    const headers = this.getHeaders();
    return this.http.put(`${this.apiUrl}/${id}`, updatedData, { headers })
      .pipe(catchError(this.handleError));
  }

  /** Remover um agendamento */
  deleteAppointment(id: number): Observable<any> {
    const headers = this.getHeaders();
    
    return this.http.delete(`${this.apiUrl}/${id}`, { headers })
      .pipe(
        catchError(err => {
          if (err.status === 200) {
            return of(null); 
          }
          return throwError(() => err);
        })
      );
  }

  getUsers(): Observable<any[]> {
    const headers = this.getHeaders();
    return this.http.get<any[]>(`${this.userApiUrl}/all`, { headers })
      .pipe(catchError(this.handleError));
  }

  /** Gerar headers com token */
  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });
  }

  /** Tratamento de erro */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Erro ao processar requisição:', error);
    return throwError(() => error);
  }
}
