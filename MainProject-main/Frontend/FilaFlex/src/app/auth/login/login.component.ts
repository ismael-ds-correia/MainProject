import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
  <section>
    <div class="login-container">
      <h2>Login</h2>
      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="login">
        <div>
          <label for="email">Email:</label>
          <input type="email" id="email" formControlName="email" placeholder="Digite seu email" />
          <span *ngIf="loginForm.get('email')?.invalid && loginForm.get('email')?.touched" class="error-message">
            Digite um e-mail válido.
          </span>
        </div>
        <div>
          <label for="password">Senha:</label>
          <input type="password" id="password" formControlName="password" placeholder="Digite sua senha" required />
          <span *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched" class="error-message">
            A senha é obrigatória.
          </span>
        </div>
        <button type="submit" [disabled]="loginForm.invalid">Login</button>
      </form>
      <p>Ainda não possui conta? <a [routerLink]="['/register']">Registre-se</a></p>
    </div>
  </section>
  `,
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.authService.login(email, password)
        .then(success => {
          if (success) {
            this.router.navigate(['/home']);
          } else {
            alert('Erro ao fazer login. Verifique suas credenciais.');
          }
        })
        .catch(err => alert('Erro ao fazer login: ' + err));
    }
  }
}