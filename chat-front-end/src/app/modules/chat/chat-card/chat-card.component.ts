import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-chat-card',
  templateUrl: './chat-card.component.html',
  styleUrls: ['./chat-card.component.css']
})
export class ChatCardComponent implements OnInit {
  avatar: string = "";

  @Input() name: string = "";
  @Input() username: string = "";
  @Output() cardClick: EventEmitter<string> = new EventEmitter<string>();

  ngOnInit() {
    this.avatar = this.name.charAt(0).toUpperCase();
  }

  onCardClick() {
    this.cardClick.emit(this.username);
  }
}
