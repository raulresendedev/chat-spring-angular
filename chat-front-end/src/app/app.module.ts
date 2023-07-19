import { HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { initializer } from 'src/init-app/app.init';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ChatHomeComponent } from './modules/chat/chat-home/chat-home.component';
import { WebSocketService } from './service/websocket.service';
import { AppPrimengModule } from './shared/modules/app-primeng.module';
import { ChatCardComponent } from './modules/chat/chat-card/chat-card.component';
import { AdicionarGrupoComponent } from './modules/chat/adicionar-grupo/adicionar-grupo.component';
import { SharedModule } from './shared/shared.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChatConversationComponent } from './modules/chat/chat-conversation/chat-conversation.component';

@NgModule({
  declarations: [
    AppComponent,
    ChatHomeComponent,
    ChatCardComponent,
    AdicionarGrupoComponent,
    ChatConversationComponent

  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    KeycloakAngularModule,
    AppPrimengModule,
    SharedModule,
    RouterModule.forRoot([])
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      deps: [KeycloakService],
      multi: true,
    }, WebSocketService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }