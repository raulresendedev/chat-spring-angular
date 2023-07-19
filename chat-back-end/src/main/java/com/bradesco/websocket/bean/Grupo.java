package com.bradesco.websocket.bean;

import com.bradesco.websocket.dto.GrupoWithUsersDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long idGrupo;
    @Column(name = "NMGRUPO", length = 50, nullable = false)
    private String nome;

    public Grupo(GrupoWithUsersDto dto){
        this.nome = dto.nome();
    }

    public Grupo(long id, GrupoWithUsersDto dto){
        this.idGrupo = id;
        this.nome = dto.nome();
    }
}
