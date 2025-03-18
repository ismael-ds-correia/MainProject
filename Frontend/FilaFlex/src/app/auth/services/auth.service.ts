import { Injectable } from '@angular/core';
import { User } from '../user';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl: string;

  constructor() {
    this.apiUrl = `${environment.apiUrl}/auth`;
    
    //logs para debug
    console.log('Auth Service usando:');
    console.log('- API URL:', this.apiUrl);
    console.log('- Ambiente:', environment.production ? 'Produção' : 'Desenvolvimento');
  }


  async login(email: string, password: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiUrl}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
        credentials: 'include'
      });
  
      if (!response.ok) {
        throw new Error('Login falhou');
      }
  
      const data = await response.json();
      localStorage.setItem('token', data.token);
      
      try {
        const decodedToken: any = jwtDecode(data.token);
        console.log('Token após login decodificado:', decodedToken);
        
        const userRole = decodedToken.role || decodedToken.authorities || 'USER';
        console.log('Role extraída do token:', userRole);
        
        localStorage.setItem('userRole', 
          typeof userRole === 'string' ? userRole : JSON.stringify(userRole)
        );
        
        console.log('Role salva no localStorage:', localStorage.getItem('userRole'));
      } catch (error) {
        console.error('Erro ao extrair role do token:', error);
      }
      
      return true;
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      return false;
    }
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  async register(newUser: User): Promise<User | null> {
    try {
      newUser.role = newUser.role.toUpperCase();
      const response = await fetch(`${this.apiUrl}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newUser)
      });
      return await response.json();
    } catch (error) {
      console.error('Erro ao registrar usuário:', error);
      return null;
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(requiredRole: string): boolean {
    const token = this.getToken();
    if (!token) {
        return false;
    }
    try {
        const decodedToken: any = jwtDecode(token);
        return decodedToken.role?.toLowerCase() === requiredRole.toLowerCase();
    } catch (error) {
        console.error('Token error:', error);
        return false;
    }
  }


  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUserId(): string | null {
    const token = this.getToken();
    if (!token) {
      console.error('Token não encontrado');
      return null;
    }
    
    try {
      const decodedToken: any = jwtDecode(token);
      console.log('Token decodificado:', decodedToken);
      
      // Verificar múltiplas possíveis propriedades que podem conter o ID do usuário
      const possibleIdFields = ['id', 'userId', 'user_id', 'sub', 'jti'];
      
      for (const field of possibleIdFields) {
        if (decodedToken[field]) {
          console.log(`ID do usuário encontrado no campo "${field}": ${decodedToken[field]}`);
          return String(decodedToken[field]);
        }
      }
      
      // Se chegou aqui, não encontrou o ID em nenhum campo comum
      console.error('Não foi possível encontrar o ID do usuário no token JWT. Campos disponíveis:', Object.keys(decodedToken));
      
      // Fallback temporário: retornar um ID fixo para testes
      // REMOVA ISSO EM PRODUÇÃO!
      console.warn('Usando ID de usuário fixo (1) para testes. REMOVA EM PRODUÇÃO!');
      return '1';
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  getCurrentUser(): { id: string; email: string; role: string } | null {
    const token = this.getToken();

    if (!token) {
        console.error('Nenhum token encontrado.');
        return null;
    }

    try {
        const decodedToken: any = jwtDecode(token);
        console.log('Token decodificado:', decodedToken);

        return {
            id: decodedToken.id || decodedToken.userId || decodedToken.sub || '', // Retorna string vazia se não encontrar um ID válido
            email: decodedToken.email || 'desconhecido',
            role: decodedToken.role || 'USER' // Default para 'USER' caso não tenha um role definido
        };
    } catch (error) {
        console.error('Erro ao decodificar token:', error);
        return null;
    }
  }
}