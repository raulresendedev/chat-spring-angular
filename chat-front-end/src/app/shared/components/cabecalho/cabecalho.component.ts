import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-cabecalho',
  templateUrl: './cabecalho.component.html',
  styleUrls: ['./cabecalho.component.css']
})
export class CabecalhoComponent implements OnInit {

  items: MenuItem[] = [];
  user = '';
  constructor(private keycloakService: KeycloakService){}

  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-fw pi-home',
        routerLink: '/'
      },
      {
        label: 'Chat',
        icon: 'pi pi-fw pi-user',
        routerLink: '/chat'
      },
      {
        label: 'Sair',
        icon: 'pi pi-fw pi-power-off',
        command: () => this.logout()
      }
    ];
  }
  logout(): void {
    window.location.href = 'http://localhost:8080/realms/websocket/protocol/openid-connect/logout';
  }

}