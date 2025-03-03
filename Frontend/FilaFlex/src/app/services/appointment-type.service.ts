import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AppointmentType {
  name: string;
  description: string;
  category: string[];
  price: number;
  estimatedTime: number;
  appointmentDate: string;
  requiredDocumentation: string[];
  address: {
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
  private apiUrl = 'http://localhost:8080/appointment-types'; // URL da API

  constructor(private http: HttpClient) {}

  getAppointmentTypes(): Observable<AppointmentType[]> {
    return this.http.get<AppointmentType[]>(`${this.apiUrl}/all`);
  }
}