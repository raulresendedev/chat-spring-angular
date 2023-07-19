import { NgModule } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { AvatarModule } from 'primeng/avatar';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { SplitButtonModule } from 'primeng/splitbutton';
import { MenuModule } from 'primeng/menu';
import { DialogModule } from 'primeng/dialog';
import { BadgeModule } from 'primeng/badge';

@NgModule({
    exports: [
      MenubarModule,
      AvatarModule,
      InputTextModule,
      ButtonModule,
      ToastModule,
      InputTextareaModule,
      SplitButtonModule,
      MenuModule,
      DialogModule,
      BadgeModule
    ]

  })
export class AppPrimengModule { }
