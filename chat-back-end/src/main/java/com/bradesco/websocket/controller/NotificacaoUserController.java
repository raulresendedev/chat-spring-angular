package com.bradesco.websocket.controller;

import com.bradesco.websocket.service.NotificacaoUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/api/notificacao")
public class NotificacaoUserController {

    private final NotificacaoUserService notificacaoUserService;

    @GetMapping("/{username}")
    public ResponseEntity<?> obterNotificacoes(@PathVariable("username") String username){
        return notificacaoUserService.obterNotificacoes(username);
    }
}
