import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import routes from './app/app.routes';
import { provideRouter } from '@angular/router';
import { importProvidersFrom } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(BrowserModule, ReactiveFormsModule),
    provideRouter(routes)
  ]
}).catch(err => console.error(err));