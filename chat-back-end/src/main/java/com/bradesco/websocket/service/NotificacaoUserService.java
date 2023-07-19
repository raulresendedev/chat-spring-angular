package com.bradesco.websocket.service;

import com.bradesco.websocket.bean.NotificacaoUser;
import com.bradesco.websocket.repository.NotificacaoUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class NotificacaoUserService {

    private final NotificacaoUserRepository notificacaoUserRepository;

    public ResponseEntity<?> obterNotificacoes(String username){
        try{
            return ResponseEntity.ok(notificacaoUserRepository.obterNotificacaoDoUsuario(username));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public NotificacaoUser atualizarVisto(Long idNotificacao){

        NotificacaoUser notificacao = notificacaoUserRepository.findById(idNotificacao)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        notificacao.setVisto(true);

        notificacaoUserRepository.save(notificacao);

        return notificacao;
    }
}
