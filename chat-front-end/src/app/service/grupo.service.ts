import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GrupoWithUsers } from '../models/grupoWithUsers';
import { Grupo } from '../models/grupo';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class GrupoService {

  private readonly API = 'api/grupos';

  constructor(private httpClient: HttpClient){}

  adicionarGrupo(data: GrupoWithUsers){
    return this.httpClient.post(this.API, data, {responseType:"text"});
  }

  editarGrupo(idGrupo:number, data: GrupoWithUsers){
    return this.httpClient.put(this.API+`/${idGrupo}`, data, {responseType:"text"});
  }

  obterGrupos(data: string){
    return this.httpClient.get<Grupo[]>(`${this.API}/${data}`)
  }
  
  obterUsuariosDoGrupo(data: number){
    return this.httpClient.get<User[]>(`${this.API}/usuarios-do-grupo/${data}`)
  }

  sairDoGrupo(idGrupo: number, username:string){
    return this.httpClient.delete(`${this.API}/${idGrupo}/${username}`, {responseType:"text"})
  }
}
