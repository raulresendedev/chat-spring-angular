import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { AcoesEnum, obterValorEnum } from 'src/app/shared/enum/acoes.enum';
import { AdicionarGrupoComponent } from '../adicionar-grupo/adicionar-grupo.component';
import { DialogService } from 'primeng/dynamicdialog';
import { GrupoService } from 'src/app/service/grupo.service';
import { Grupo } from 'src/app/models/grupo';
import { Router } from '@angular/router';
import { WebSocketService } from 'src/app/service/websocket.service';
import { MenuItem, MessageService } from 'primeng/api';
import { notificacaoService } from 'src/app/service/notificacao.service';
import { notificacao } from 'src/app/models/notificacao';

@Component({
  selector: 'app-chat-home',
  templateUrl: './chat-home.component.html',
  styleUrls: ['./chat-home.component.css']
})
export class ChatHomeComponent implements OnInit {
  @ViewChild('cabecalho', { static: true }) cabecalho!: ElementRef;

  gruposUsuario!: Grupo[];
  notificacoesUsuario: notificacao[] = [];
  grupoSelecionado?: Grupo;
  acaoSelecionada: AcoesEnum | undefined;
  items!: MenuItem[];
  geeks: boolean = false;
  
  constructor(
    private authService: AuthService,
    private grupoService: GrupoService,
    private dialogService: DialogService,
    private router: Router,
    private webSocketService: WebSocketService,
    private messageService: MessageService,
    private notificacaoService: notificacaoService
  ) { 
  }
  
  abrirModalAdicionar(acao: string, data: any | null) {

    this.acaoSelecionada = obterValorEnum(acao)
    const ref = this.dialogService.open(AdicionarGrupoComponent, {
      header: `${acao.charAt(0).toUpperCase()}${acao.slice(1).toLowerCase()} Grupo`,
      width: '80%',
      height: '100%',
      data: [
        data, acao
      ]
    })
  }

  abrirModalEditar(acao: string, data: any | null) {

    this.acaoSelecionada = obterValorEnum(acao)
    const ref = this.dialogService.open(AdicionarGrupoComponent, {
      header: `${acao.charAt(0).toUpperCase()}${acao.slice(1).toLowerCase()} Grupo`,
      width: '80%',
      height: '100%',
      data: [
        data, acao
      ]
    })
  }

  ngOnInit(): void {
    this.carregarGrupos();
    this.carregarNotificacao();
    this.connect();

    this.items = [
        {
            label: 'Editar Grupo',
            icon: 'pi pi-pencil',
            command: () => {
              this.abrirModalEditar("EDITAR", this.grupoSelecionado)
            }
        },
        {
            label: 'Sair do Grupo',
            icon: 'pi pi-sign-out',
            command: () => {
            }
        }
  ];

  }

  carregarGrupos(){
    
    this.grupoService.obterGrupos(this.authService.getUserName()).subscribe(
      (response) =>{
        this.gruposUsuario = response
      },
      (error) => {

      }
    )
  }

  carregarNotificacao(){
    
    this.notificacaoService.obterNotificacao(this.authService.getUserName()).subscribe(
      (response) =>{
        this.notificacoesUsuario = response
      },
      (error) => {

      }
    )
  }

  private stompClient: any;

  async connect(): Promise<void> {
    return new Promise(async (resolve, reject) => {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.disconnect();
      }
    
      const token = await this.authService.getUserToken();
      this.stompClient = this.webSocketService.Connect();
      
      this.stompClient.connect(
        {
          "X-Authorization": "Bearer " + token
        },
        (frame: any) => {
          
          this.stompClient.subscribe(`/topic/grupo/${this.authService.getUserName()}`, (notifications: any) => {
            const updatedGruposUsuario = JSON.parse(notifications.body) as Grupo[];
          
            if (this.grupoSelecionado) {
              const grupoEncontrado = updatedGruposUsuario.find(grupo => grupo.idGrupo === this.grupoSelecionado?.idGrupo);
          
              if (!grupoEncontrado) {
                this.messageService.add({ severity: 'error', summary: 'Removido', detail: 'Você for removido do grupo ' + this.grupoSelecionado.nome +"!" });
                this.grupoSelecionado = undefined;
                this.router.navigate(['chat']);
              }
            }
          
            const grupoSelecionadoAtualizado = updatedGruposUsuario.find(grupo => grupo.idGrupo === this.grupoSelecionado?.idGrupo);
          
            if (grupoSelecionadoAtualizado) {
              this.grupoSelecionado = grupoSelecionadoAtualizado;
            }
          
            this.gruposUsuario = updatedGruposUsuario;
          });
          
          this.stompClient.subscribe(`/topic/notificacao/${this.authService.getUserName()}`, (notifications: any) => {
            
            const novaNotificacao = JSON.parse(notifications.body) as notificacao;

            const indice = this.notificacoesUsuario.findIndex((notificacao) => notificacao.idNotificaoUser === novaNotificacao.idNotificaoUser);
            
            if (indice !== -1) {
              this.notificacoesUsuario[indice] = novaNotificacao;
            } else {
              this.notificacoesUsuario.push(novaNotificacao);
            }
          });
          
          resolve();
        },
        (error: any) => {
          reject(error);
        }
      );
    });
  }

  closeGrupo(){
    this.grupoSelecionado = undefined
    this.router.navigate(['chat']);
  }

  ngOnDestroy(){
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect();
    }
  }

  selectGrupo(grupo:Grupo){
    this.grupoSelecionado = grupo
    this.router.navigate(['chat/grupo', grupo.idGrupo]);
  }

  showNotifications(){
    this.geeks = true;
  }

  marcarNotificacaoComoVista(notificacao: notificacao){
    if(!notificacao.visto)
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(
          `/current/notificacao/atualizar/${notificacao.idNotificaoUser}`,
          {}
        );
      } else {
        console.log('A conexão do cliente Stomp não está estabelecida.');
      }
  }

  notificacoesNaoVistas() {
      const quantidadeNotificacoes = this.notificacoesUsuario.filter(notificacao => !notificacao.visto).length;
      return quantidadeNotificacoes !== 0 ? quantidadeNotificacoes : null;
  }
}