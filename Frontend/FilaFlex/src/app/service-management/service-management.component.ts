import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-queue-management',
  template: `
    <div class="p-6">
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">Gerenciamento de Filas de Atendimento</h1>
        <button (click)="handleAddQueue()" class="btn btn-primary">Adicionar Fila</button>
      </div>
      
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
              <button (click)="handleEditQueue(queue.id)" class="btn btn-outline">Editar</button>
              <button (click)="handleDeleteQueue(queue.id)" class="btn btn-danger">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
    styleUrl: './service-management.component.css'
})
export class QueueManagementComponent implements OnInit {
  queues = [
    //Exemplo de dados - Substituir por dados reais futuramente.
    { id: 1, name: 'Fila de Emergência', type: 'Hospitalar', priority: 'Alta' },
    { id: 2, name: 'Fila de Atendimento Bancário', type: 'Financeiro', priority: 'Média' },
    { id: 3, name: 'Fila de Suporte Técnico', type: 'Tecnologia', priority: 'Baixa' }
  ];

  constructor() {}

  ngOnInit(): void {}

  handleAddQueue(): void {
    console.log('Adicionar nova fila');
  }

  handleEditQueue(id: number): void {
    console.log('Editar fila:', id);
  }

  handleDeleteQueue(id: number): void {
    console.log('Excluir fila:', id);
  }
}
