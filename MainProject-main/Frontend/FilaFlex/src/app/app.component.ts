import { Component } from '@angular/core';
import { RegisterComponent } from './auth/register/register.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RegisterComponent],
  template: ` <app-register></app-register> `,
})
export class AppComponent {
  title = 'FilaFlex';
}
