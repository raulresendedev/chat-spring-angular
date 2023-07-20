import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { notificacao } from '../models/notificacao';

@Injectable({
  providedIn: 'root'
})
export class notificacaoService {

  private readonly API = 'api/notificacao';

  constructor(private httpClient: HttpClient){}

  obterNotificacao(data: string){
    return this.httpClient.get<notificacao[]>(`${this.API}/${data}`)
  }

  apagarNotificacao(idNotificacao: number){
    return this.httpClient.delete(`${this.API}/${idNotificacao}`, {responseType:"text"})
  }
}
