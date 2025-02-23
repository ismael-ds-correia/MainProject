import { Component } from '@angular/core';

@Component({
  selector: 'app-unauthorized',
  template: `<h1>Acesso Negado</h1><p>Você não tem permissão para acessar esta página.</p>`,
  styleUrls: ['./unauthorized.component.css']
})
export class UnauthorizedComponent {}