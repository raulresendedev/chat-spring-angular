import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Mensagem } from '../models/mensagem';

@Injectable({
  providedIn: 'root'
})
export class MensagemService {

  private readonly API = 'api/message';

  constructor(private httpClient: HttpClient){}

  obterMensagens(idGrupo:number, pagina:number){
    return this.httpClient.get<any>(`${this.API}/${idGrupo}/${pagina}`)
  }
}
