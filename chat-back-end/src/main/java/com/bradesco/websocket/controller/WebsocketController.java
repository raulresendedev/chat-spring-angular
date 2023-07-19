package com.bradesco.websocket.controller;

import com.bradesco.websocket.bean.NotificacaoUser;
import com.bradesco.websocket.service.NotificacaoUserService;
import com.bradesco.websocket.service.WebsocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class WebsocketController {

    private final WebsocketService service;
    private final NotificacaoUserService notificacaoUserService;

    @MessageMapping("/grupo/{idGrupo}")
    public void getMessage(final String message, @DestinationVariable String idGrupo) throws JsonProcessingException {
        service.notifyGroup(idGrupo, message);
    }

    @MessageMapping("/notificacao/atualizar/{idNotificacao}")
    public void updateNotificacao(@DestinationVariable long idNotificacao) throws JsonProcessingException {

        NotificacaoUser n = notificacaoUserService.atualizarVisto(idNotificacao);

        service.enviarNotificacaoUser(n);
    }

}
