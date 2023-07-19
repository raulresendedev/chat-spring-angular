import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private readonly API = 'api/admin';

  constructor(private httpClient: HttpClient){}

  buscarUsuario(data: string, exact:boolean){
    return this.httpClient.get<User[]>(`${this.API}/${data}/${exact}`);
  }
}
