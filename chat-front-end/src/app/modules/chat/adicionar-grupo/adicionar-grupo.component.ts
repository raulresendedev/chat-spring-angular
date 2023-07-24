import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { AuthService } from 'src/app/auth/auth.service';
import { GrupoWithUsers } from 'src/app/models/grupoWithUsers';
import { User } from 'src/app/models/user';
import { ChatService } from 'src/app/service/chat.service';
import { GrupoService } from 'src/app/service/grupo.service';
import { AcoesEnum } from 'src/app/shared/enum/acoes.enum';

@Component({
  selector: 'app-adicionar-grupo',
  templateUrl: './adicionar-grupo.component.html',
  styleUrls: ['./adicionar-grupo.component.css']
})
export class AdicionarGrupoComponent implements OnInit {

  @ViewChild('searchResults', { static: false }) searchResults?: ElementRef;
  
  acaoSelecionada: AcoesEnum | undefined;
  grupoSelecionado: GrupoWithUsers;
  usuarioLogado = this.authService.getUserName()
  usuarios: User[] = [];
  usuariosSelecionados: User[] = [];

  formulario!: FormGroup;
  formularioValido: boolean = false;

  constructor(
      private formBuilder: FormBuilder,
      private config: DynamicDialogConfig,
      private chatService: ChatService,
      private ref: DynamicDialogRef,
      private grupoService: GrupoService,
      private messageService: MessageService,
      private authService: AuthService
    ) {
    
    this.acaoSelecionada = this.config.data[1];
    this.grupoSelecionado = this.config.data[0];

    this.formulario = this.formBuilder.group({});

    this.formulario = this.formBuilder.group({
      nome: [{ value: this.grupoSelecionado?.nome, disabled: this.isExibir() }, [Validators.required, Validators.maxLength(50), Validators.minLength(5)] ]
    })
   }

  ngOnInit(): void {
    this.formulario.valueChanges.subscribe(() => {
      this.formularioValido = this.formulario.valid;
    });
    
    this.usuariosSelecionados.push(this.authService.getUser())

    if(this.isEditar()){
      this.usersDoGrupo()
      if(this.formulario.value.nome.length >= 5 && this.validarUsuariosSelecionados()){
        this.formularioValido = true
      }
    }
  }

  validarUsuariosSelecionados(){
    if(this.usuariosSelecionados.length > 1){
      return false
    }
    return true
  }

  @HostListener('document:click', ['$event.target'])
  onDocumentClick(target: HTMLElement) {
    if (this.searchResults && !this.searchResults.nativeElement?.contains(target)) {
      this.usuarios = []
    }
  }

  onCardClicked(element: User) {
    this.usuarios = []
    this.usuariosSelecionados.push(element);
  }

  searchUsers(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const searchTerm = inputElement.value;
    
    if(searchTerm != ""){
      this.chatService.buscarUsuario(searchTerm, false).subscribe(
        (data) =>{
          this.usuarios = data
          this.usuarios = this.usuarios.filter((usuario) => {
            return !this.usuariosSelecionados.some((selecionado) => selecionado.username === usuario.username);
          });
        },
        (error) =>{

        });
    }else{
      this.usuarios = []
    }
  }

  usersDoGrupo(){
    this.grupoService.obterUsuariosDoGrupo(this.grupoSelecionado.idGrupo).subscribe(
      (response => {
        this.usuariosSelecionados = response.filter((usuario) => usuario.username == this.usuarioLogado? usuario.nome = "Você": usuario);
      })
    )
  }

  adicionar(){
    const formData = this.formulario.value as GrupoWithUsers
    formData.users = this.usuariosSelecionados
    
    this.grupoService.adicionarGrupo(formData).subscribe(
      (response) => {
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Grupo criado com sucesso!' });
        this.ref.close(true)
      },
      (error) =>{
        console.log("erro")
      }
    )
  }

  editar(){
    const formData = this.formulario.value as GrupoWithUsers
    formData.users = this.usuariosSelecionados
    this.grupoService.editarGrupo(this.grupoSelecionado.idGrupo, formData).subscribe(
      (response) => {
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Grupo editado com sucesso!' });
        this.ref.close(true)
      },
      (error) =>{
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: error.error });
      }
    )
  }

  removerUserSelecionado(element: User){
    this.usuariosSelecionados = this.usuariosSelecionados.filter((x) => {
      return x.username !== element.username;
    })
  }

  fecharModal() {
    this.ref.close();
  }

  isExibir = (): boolean => this.acaoSelecionada?.toUpperCase() === AcoesEnum.EXIBIR;
  isAdicionar = (): boolean => this.acaoSelecionada?.toUpperCase() === AcoesEnum.ADICIONAR;
  isEditar = (): boolean => this.acaoSelecionada?.toUpperCase() === AcoesEnum.EDITAR;

  loading: boolean = false;
}
