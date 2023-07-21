package com.bradesco.websocket.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long idNotificaoUser;
    @Column(name = "USERNAME", nullable = false)
    private String username;
    @Column(name = "MENSAGEM", nullable = false)
    private String mensagem;
    @Column(name = "VISTO", nullable = false)
    private boolean visto;
    @Column(name = "DATA", nullable = false)
    private Date data;

    public NotificacaoUser(String username, String mensagem){
        this.username = username;
        this.mensagem = mensagem;
        this.visto = false;
        this.data = new Date();
    }
}
