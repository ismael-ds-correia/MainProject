import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';

//Interface para o que é recebido da API.
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
  status: string;
  checkInTime?: string;
  startTime?: string;
  endTime?: string;
  queueOrder: number;
  priorityCondition: string;
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
export class QueueService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = `${environment.apiUrl}/queue`;

    console.log('QueueService usando environment:');
    console.log('- API URL:', this.apiUrl);
    console.log('- Ambiente:', environment.production ? 'Produção' : 'Desenvolvimento');
  }
  
  //Método para obter fila por tipo de agendamento.
  getQueueByAppointmentType(name: string): Observable<AppointmentResponse[]> {
    console.log(`Buscando fila para o serviço: ${name}`);
    console.log(`URL completa: ${this.apiUrl}/appointment-type/${name}`);
    
    const token = localStorage.getItem('token');
    console.log('Token a ser usado:', token ? `${token.substring(0, 15)}...` : 'nenhum');
    
    const headers = { 
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}` 
    };
    
    return this.http.get<AppointmentResponse[]>(
      `${this.apiUrl}/appointment-type/${name}`, 
      { headers }
    ).pipe(catchError(this.handleError));
  }

  //Método para obter fila por ID do usuário.
  reorderQueue(id: number, position: number): Observable<string> {
    console.log(`Reordenando agendamento ${id} para posição ${position}`);
    console.log(`URL completa: ${this.apiUrl}/${id}/position/${position}`);

    const headers = this.getHeaders();
    
    return this.http.put<string>(`${this.apiUrl}/${id}/position/${position}`, {}, { headers, responseType: 'text' as 'json' })
      .pipe(catchError(this.handleError));
  }

  //Método para chamar próximo da fila.
  callNextAppointment(appointmentTypeId: number, appointmentTypeName: string): Observable<AppointmentResponse> {
    console.log(`Verificando se já existe um agendamento em atendimento para: ${appointmentTypeName}`);
    
    // Primeiro buscamos a fila usando o NOME para verificar se há um em atendimento
    return this.http.get<AppointmentResponse[]>(
      `${this.apiUrl}/appointment-type/${appointmentTypeName}`,
      { headers: this.getHeaders() }
    ).pipe(
      map(queue => {
        // Verificar se já existe um agendamento em atendimento
        const attendingAppointment = this.findAttendingAppointment(queue);
        
        if (attendingAppointment) {
          console.log('Já existe um agendamento em atendimento:', attendingAppointment);
          return attendingAppointment;
        } else {
          // Se não, chamamos o próximo normalmente usando o ID
          console.log('Nenhum agendamento em atendimento. Chamando o próximo.');
          throw { callNext: true, appointmentTypeId };
        }
      }),
      catchError(error => {
        // Se for nossa exceção personalizada, chamamos o próximo
        if (error && error.callNext) {
          console.log(`Chamando próximo agendamento do tipo ${error.appointmentTypeId}`);
          return this.http.put<AppointmentResponse>(
            `${this.apiUrl}/appointment-type/${error.appointmentTypeId}/next`, 
            {}, 
            { headers: this.getHeaders() }
          ).pipe(catchError(this.handleError));
        }
        // Caso contrário, propague o erro
        return throwError(() => error);
      })
    );
  }

  //Método para completar um agendamento.
  completeAppointment(appointmentId: number): Observable<AppointmentResponse> {
    console.log(`Completando agendamento ${appointmentId}`);
    console.log(`URL completa: ${this.apiUrl}/appointment/${appointmentId}/complete`);

    const headers = this.getHeaders();
    
    return this.http.put<AppointmentResponse>(`${this.apiUrl}/appointment/${appointmentId}/complete`, {}, { headers })
      .pipe(catchError(this.handleError));
  }

  //Método para realizar check-in.
  registerCheckIn(appointmentId: number): Observable<AppointmentResponse> {
    console.log(`Registrando check-in para agendamento ${appointmentId}`);
    console.log(`URL completa: ${this.apiUrl}/appointment/${appointmentId}/check-in`);

    const headers = this.getHeaders();
    
    return this.http.put<AppointmentResponse>(`${this.apiUrl}/appointment/${appointmentId}/check-in`, {}, { headers })
      .pipe(catchError(this.handleError));
  }

  //Método para marcar agendamento como ausente
  markAsAbsent(appointmentId: number): Observable<AppointmentResponse> {
    console.log(`Marcando agendamento ${appointmentId} como ausente`);
    console.log(`URL completa: ${this.apiUrl}/appointment/${appointmentId}/absent`);

    const headers = this.getHeaders();
    
    return this.http.put<AppointmentResponse>(`${this.apiUrl}/appointment/${appointmentId}/absent`, {}, { headers })
      .pipe(catchError(this.handleError));
  }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    });
  }

  findAttendingAppointment(queue: AppointmentResponse[]): AppointmentResponse | null {
    return queue.find(appointment => appointment.status === 'ATTENDING') || null;
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Erro ao processar requisição:', error);
    return throwError(() => error);
  }
}
