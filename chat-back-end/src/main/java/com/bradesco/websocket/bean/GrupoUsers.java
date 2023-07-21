package com.bradesco.websocket.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GrupoUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long idGrupoUsers;

    @Column(name = "IDGRUPO", nullable = false)
    private long idGrupo;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    public GrupoUsers(long idGrupo, String username){
        this.idGrupo = idGrupo;
        this.username = username;
    }
}
