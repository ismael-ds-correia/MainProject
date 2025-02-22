import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})

export class RegisterComponent {
  register: FormGroup;
  errorMessage: string = '';

  constructor(private fb: FormBuilder) {
    this.register = this.fb.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(6),
          Validators.pattern(/^[A-Za-zÀ-ÿ]+(?:\s[A-Za-zÀ-ÿ]+)+$/),
        ],
      ],
      password: ['', [Validators.required, Validators.minLength(8)]],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    if (this.register.valid) {
      console.log('Form submitted:', this.register.value);
    } else {
      this.errorMessage = 'Please fill in all required fields correctly!';
    }
  }
}
