import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatHomeComponent } from './chat-home/chat-home.component';
import { AppPrimengModule } from 'src/app/shared/modules/app-primeng.module';
import { ChatCardComponent } from './chat-card/chat-card.component';
import { ChatConversationComponent } from './chat-conversation/chat-conversation.component';



@NgModule({
  declarations: [
    ChatHomeComponent,
    ChatCardComponent,
    ChatConversationComponent
  ],
  imports: [
    CommonModule,
    AppPrimengModule
  ]
})
export class ChatModule { }
