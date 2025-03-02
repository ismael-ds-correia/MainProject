import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-service-management',
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">Gerenciamento de Filas de Atendimento</h1>
        <button (click)="handleAddQueue()" class="btn btn-primary">Adicionar Fila</button>
      </div>
      
      <app-queue-table [queues]="queues" (editQueue)="handleEditQueue($event)" (deleteQueue)="handleDeleteQueue($event)"></app-queue-table>
    </div>
  `,
  styles: [
    `
      .btn {
        padding: 8px 12px;
        border-radius: 5px;
        cursor: pointer;
      }
      .btn-primary {
        background-color: #007bff;
        color: white;
      }
    `
  ]
})
export class ServiceManagementComponent implements OnInit {
  queues: any[] = [];
  private apiUrl = 'http://localhost:3000/queues'; // URL da API para testes no Insomnia

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadQueues();
  }

  loadQueues(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(
      (data) => this.queues = data,
      (error) => console.error('Erro ao carregar filas:', error)
    );
  }

  handleAddQueue(): void {
    const newQueue = { name: 'Nova Fila', type: 'Outro', priority: 'Média' };
    this.http.post<any>(this.apiUrl, newQueue).subscribe(
      (data) => this.queues = [...this.queues, data],
      (error) => console.error('Erro ao adicionar fila:', error)
    );
  }

  handleEditQueue(id: number): void {
    const updatedQueue = { name: 'Fila Atualizada', type: 'Outro', priority: 'Alta' };
    this.http.put<any>(`${this.apiUrl}/${id}`, updatedQueue).subscribe(
      () => this.loadQueues(),
      (error) => console.error('Erro ao editar fila:', error)
    );
  }

  handleDeleteQueue(id: number): void {
    this.http.delete(`${this.apiUrl}/${id}`).subscribe(
      () => this.queues = this.queues.filter(queue => queue.id !== id),
      (error) => console.error('Erro ao excluir fila:', error)
    );
  }
}
