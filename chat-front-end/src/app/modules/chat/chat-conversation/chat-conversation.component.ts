import { AfterViewChecked, Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';
import { Mensagem } from 'src/app/models/mensagem';
import { WebSocketService } from 'src/app/service/websocket.service';
import { MensagemService } from 'src/app/service/mensagem.service';

@Component({
  selector: 'app-chat-conversation',
  templateUrl: './chat-conversation.component.html',
  styleUrls: ['./chat-conversation.component.css']
})
export class ChatConversationComponent implements OnChanges {

  @ViewChild('messagecontainer') chatContainer!: ElementRef;

  userLogado = this.authService.getUserName();
  grupoSelecionado!:number;
  inputValue: string = '';
  greetings: Mensagem[] = [];
  pagina = 0;
  semMaisMensagens = false

  private stompClient: any;

  constructor(
    private webSocketService: WebSocketService,
    private messageservice: MensagemService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) { }

  ngAfterViewInit() {
    this.chatContainer.nativeElement.addEventListener('scroll', this.handleScroll);
  }
  private scrollDebounceTimer: ReturnType<typeof setTimeout> | null = null;
  handleScroll = (event: Event) => {
    const element = event.target as HTMLElement;
    const scrollPosition = (element.scrollHeight + element.scrollTop) - element.clientHeight;
    const scrollHeight = element.scrollHeight;
    const scrollPercentage = (scrollPosition / scrollHeight) * 100;
    const debounceTime = 100;

    if (this.scrollDebounceTimer) {
      clearTimeout(this.scrollDebounceTimer);
    }

    this.scrollDebounceTimer = setTimeout(() => {
      if (scrollPercentage <= 15 && !this.semMaisMensagens) {
        this.obterMensagens(this.grupoSelecionado);
      }
      this.scrollDebounceTimer = null;
    }, debounceTime);
  }
  
  

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.grupoSelecionado = params['grupoId'];
      this.greetings = []
      this.pagina = 0;
      this.semMaisMensagens = false
      this.obterMensagens(this.grupoSelecionado);
      this.connect();
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['grupoSelecionado']) {

    }
  }

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
          
          this.stompClient.subscribe(`/topic/grupo/${this.grupoSelecionado}`, (notifications: any) => {
            this.showMessage(notifications.body);
          });
          
          resolve();
        },
        (error: any) => {
          reject(error);
        }
      );
    });
  }
  
  ngOnDestroy(){
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect();
      this.greetings = []
    }
  }
  
  sendMessage() {

    const msg: Mensagem = {
      idMensagem: 0,
      username: this.authService.getUserName(),
      idGrupo: this.grupoSelecionado,
      mensagem: this.inputValue, 
      data: new Date(),
      notificacao: false
    };

    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send(
        `/current/grupo/${this.grupoSelecionado}`,
        {},
        JSON.stringify(msg)
      );
    } else {
      console.log('A conexão do cliente Stomp não está estabelecida.');
    }
    this.inputValue = ""
  
  }

  showMessage(message: string) {
    const mensagemObj = JSON.parse(message);
    
    const chatContainerElement = this.chatContainer.nativeElement;
    
    if(chatContainerElement.scrollTop < -30){
      this.greetings.unshift(mensagemObj);
    }else{
      this.greetings.unshift(mensagemObj);
      chatContainerElement.scrollTop = 0
    }
    
  }

  obterMensagens(idGrupo:number){
    this.messageservice.obterMensagens(idGrupo,  this.pagina ).subscribe(
      (response) => {
        if(this.greetings.length == 0){
          this.greetings = response.messages;
        }else{
          this.greetings.push(...response.messages);
        }

        if (response.messages.length == 0){
          this.semMaisMensagens = true;
        }

        this.pagina++;
      }
    );
  }

}
