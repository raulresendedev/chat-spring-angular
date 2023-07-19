package com.bradesco.websocket.service;

import com.bradesco.websocket.bean.Message;
import com.bradesco.websocket.bean.NotificacaoUser;
import com.bradesco.websocket.bean.ResponseMessage;
import com.bradesco.websocket.repository.GrupoRepository;
import com.bradesco.websocket.repository.MessageRepository;
import com.bradesco.websocket.repository.NotificacaoUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebsocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final GrupoRepository grupoRepository;
    private final NotificacaoUserRepository notificacaoUserRepository;

    public void notifyGroup(String idGrupo, final String messageChat) throws JsonProcessingException {
        ResponseMessage responseMessage = new ResponseMessage(messageChat);

        ObjectMapper objectMapper = new ObjectMapper();

        Message message = objectMapper.readValue(messageChat, Message.class);
        messageRepository.save(message);

        simpMessagingTemplate.convertAndSend("/topic/grupo/"+idGrupo, responseMessage);
    }

    public void notifyGroup(String username){
        simpMessagingTemplate.convertAndSend("/topic/grupo/"+username, grupoRepository.obterGruposDoUsuario(username) );
    }

    public void enviarNotificacaoUser(NotificacaoUser notificacao){
        simpMessagingTemplate.convertAndSend("/topic/notificacao/"+notificacao.getUsername(), notificacao);
    }

}
