import { Injectable } from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { AuthService } from '../auth/auth.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private stompClient!: Stomp.Client;

  constructor(private authService: AuthService, private httpClient: HttpClient) {}

  public Connect() {

    const ws = new SockJS("http://localhost:9090/ws");
    
    this.stompClient = Stomp.over(ws);
    this.stompClient.heartbeat.outgoing = 0; 
    
    return this.stompClient;
  }
}
