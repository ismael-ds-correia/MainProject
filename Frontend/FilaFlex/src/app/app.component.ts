import { Component } from '@angular/core';
import { RegisterFormComponent } from './register-form/register-form.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RegisterFormComponent],
  template: ` <app-register-form></app-register-form> `,
})
export class AppComponent {
  title = 'FilaFlex';
}
