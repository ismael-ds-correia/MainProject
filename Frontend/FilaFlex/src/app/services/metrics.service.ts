import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface MetricsDTO {
  totalAppointmentsCompleteds: number;
  averageWaitingTime: number;
  averageServiceTime: number;
}

@Injectable({
  providedIn: 'root'
})
export class MetricsService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = `${environment.apiUrl}/metrics`;
    
    console.log('MetricsService usando environment:');
    console.log('- API URL:', this.apiUrl);
  }

  getMetricsByAppointmentType(
    appointmentTypeName: string,
    startDate?: Date,
    endDate?: Date
  ): Observable<MetricsDTO> {
    console.log(`Buscando métricas para o serviço: ${appointmentTypeName}`);
    
    let params = new HttpParams();
    if (startDate) {
      params = params.set('startDate', startDate.toISOString());
    }
    if (endDate) {
      params = params.set('endDate', endDate.toISOString());
    }
    
    const headers = { 
      'Authorization': `Bearer ${localStorage.getItem('token')}` 
    };
    
    return this.http.get<MetricsDTO>(
      `${this.apiUrl}/appointment-type/${appointmentTypeName}`, 
      { headers, params }
    ).pipe(
      tap(data => console.log('Dados de métricas recebidos:', data)),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Erro ao buscar métricas:', error);
    return throwError(() => error);
  }
}