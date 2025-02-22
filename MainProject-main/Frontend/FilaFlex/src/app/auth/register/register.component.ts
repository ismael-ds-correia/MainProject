import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form [formGroup]="register" (ngSubmit)="onSubmit()" class="register">
      <h1>Registrar-se</h1>

      <div>
        <label for="name">Nome Completo:</label>
        <input type="text" id="name" formControlName="name" />
        <span
          *ngIf="
            register.get('name')?.invalid && register.get('name')?.touched
          "
        >
          O nome completo é obrigatório e deve ter no mínimo 6 caracteres.
        </span>
      </div>

      <div>
        <label for="email">Email:</label>
        <input type="email" id="email" formControlName="email" />
        <span
          *ngIf="
            register.get('email')?.invalid && register.get('email')?.touched
          "
        >
          Digite um e-mail válido.
        </span>
      </div>

      <div>
        <label for="password">Senha:</label>
        <input type="password" id="password" formControlName="password" />
        <span
          *ngIf="
            register.get('password')?.invalid &&
            register.get('password')?.touched
          "
        >
          A senha deve ter pelo menos 8 caracteres.
        </span>
      </div>

      <div>
        <label for="role">Perfil de usuário</label>
        <select id="role" formControlName="role">
          <option value="" disabled selected>Selecione um perfil</option>
          <option value="admin">Admin</option>
          <option value="user">Usuário</option>
        </select>
      </div>

      <button type="submit" [disabled]="register.invalid">Registrar-se</button>
    </form>
  `,
  styleUrls: ['./register.component.css']
})

export class RegisterComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  register: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(6)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['', Validators.required]
  });

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.register.valid) {
      const { name, email, password, role } = this.register.value;
      const newUser = { id: 0, name, email, password, role };

      this.authService.register(newUser)
        .then(() => this.router.navigate(['/register']))
        .catch(err => alert('Erro ao registrar usuário: ' + err));
    }
  }
}