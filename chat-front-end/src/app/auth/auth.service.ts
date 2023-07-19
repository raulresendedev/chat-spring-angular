import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private keycloakService: KeycloakService) {}

  getUserName(): string {
    return this.keycloakService.getUsername();
  }
  logout(): void {
    this.keycloakService.logout();
  }
  getLoggedUser(): any {
    return this.keycloakService.getKeycloakInstance().idTokenParsed;
  }

  getUser():User{
    
    const login = this.keycloakService.getKeycloakInstance().profile

    const user:User = {
      nome: "Você",
      username: login?.username || '',
      email: login?.email || ''
    }
    return user;
  }


  getUserToken(): Promise<any> {
    const keycloakInstance = this.keycloakService.getKeycloakInstance();
    const token = keycloakInstance.token;
    const tokenExpired = keycloakInstance.isTokenExpired();
  
    if (tokenExpired) {
      return new Promise((resolve, reject) => {
        keycloakInstance.updateToken(5)
          .then((refreshed) => {
            if (refreshed) {
              resolve(keycloakInstance.token);
            } else {
              reject('Não foi possível atualizar o token do Keycloak');
            }
          })
          .catch((error) => {
            reject(error);
          });
      });
    } else {
      return Promise.resolve(token);
    }
  }
  
}
