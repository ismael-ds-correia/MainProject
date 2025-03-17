import { Injectable } from '@angular/core';

declare global {
  interface Window {
    __env?: {
      apiUrl?: string;
      production?: boolean;
      appVersion?: string;
    };
  }
}

@Injectable({
  providedIn: 'root'
})
export class EnvService {
  //Valores padrão
  private _apiUrl = 'http://localhost:8080';
  private _production = false;
  private _appVersion = '1.0.0';

  constructor() {
    //Carrega valores do objeto window.__env (definido pelo env.js)
    const env = window['__env'] || {};
    
    //Atribui valores se existirem no window.__env
    if (env.apiUrl) this._apiUrl = env.apiUrl;
    if (env.production !== undefined) this._production = env.production;
    if (env.appVersion) this._appVersion = env.appVersion;
    
    //log para debug
    console.log('EnvService inicializado:');
    console.log('- API URL:', this._apiUrl);
    console.log('- Ambiente:', this._production ? 'Produção' : 'Desenvolvimento');
    console.log('- Versão:', this._appVersion);
  }

  get apiUrl(): string {
    return this._apiUrl;
  }

  get isProduction(): boolean {
    return this._production;
  }

  get appVersion(): string {
    return this._appVersion;
  }
  
  // Método para verificar ambiente
  get environmentName(): string {
    return this._production ? 'Produção' : 'Desenvolvimento';
  }
}