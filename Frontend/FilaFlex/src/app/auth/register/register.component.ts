import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { User } from '../user';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  register: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(6)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['', Validators.required]
  });

  onSubmit() {
    if (this.register.valid) {
      const { name, email, password, role } = this.register.value;

      const newUser: User = {
        name,
        email,
        password,
        role
      };

      this.authService.register(newUser)
        .then(success => {
          if (success) {
            this.router.navigate(['/login']);
          } else {
            alert('Erro ao registrar. Tente novamente.');
          }
        })
        .catch(err => alert('Erro ao registrar: ' + err));
    }
  }
}
