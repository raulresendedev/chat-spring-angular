import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ChatHomeComponent } from './modules/chat/chat-home/chat-home.component';
import { ChatConversationComponent } from './modules/chat/chat-conversation/chat-conversation.component';

const routes: Routes = [
  {
    path: 'chat',
    component: ChatHomeComponent,
    children: [
      { path: 'grupo/:grupoId', component: ChatConversationComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
