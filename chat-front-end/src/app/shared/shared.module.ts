import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppPrimengModule } from './modules/app-primeng.module';
import { CabecalhoComponent } from './components/cabecalho/cabecalho.component';
import { DialogService } from 'primeng/dynamicdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [CabecalhoComponent],
  imports: [
    CommonModule,
    AppPrimengModule
  ],
  exports: [ 
    CabecalhoComponent, 
    FormsModule, 
    ReactiveFormsModule, 
    RouterModule
  ],

  providers:[
    DialogService, 
    ConfirmationService,
    MessageService
  ]
})
export class SharedModule { }
