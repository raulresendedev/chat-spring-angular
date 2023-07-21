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
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long idMensagem;

    @Column(name = "IDGRUPO", nullable = false)
    private long idGrupo;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "MENSAGEM", nullable = false, length = 1500)
    private String mensagem;

    @Column(name="NOTIFICACAO", nullable = false)
    private boolean notificacao;

    @Column(name = "DATA", nullable = false)
    private Date data;

    public Message(long idGrupo, String mensagem){
        this.idGrupo = idGrupo;
        this.username = "INFO";
        this.mensagem = mensagem;
        this.notificacao = true;
        this.data = new Date();
    }

}
