import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-queue-table',
  template: `
    <table class="table-auto w-full border-collapse border border-gray-300">
      <thead>
        <tr class="bg-gray-100">
          <th class="border p-2">ID</th>
          <th class="border p-2">Nome</th>
          <th class="border p-2">Tipo de Atendimento</th>
          <th class="border p-2">Prioridade</th>
          <th class="border p-2">Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let queue of queues" class="border">
          <td class="border p-2">{{ queue.id }}</td>
          <td class="border p-2">{{ queue.name }}</td>
          <td class="border p-2">{{ queue.type }}</td>
          <td class="border p-2">{{ queue.priority }}</td>
          <td class="border p-2 flex gap-2">
            <button (click)="editQueue.emit(queue.id)" class="btn btn-outline">Editar</button>
            <button (click)="deleteQueue.emit(queue.id)" class="btn btn-danger">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>
  `,
  styles: './queue-table.component.css'
})
export class QueueTableComponent implements OnInit {
  @Input() queues: any[] = [];
  @Output() editQueue = new EventEmitter<number>();
  @Output() deleteQueue = new EventEmitter<number>();

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
}
