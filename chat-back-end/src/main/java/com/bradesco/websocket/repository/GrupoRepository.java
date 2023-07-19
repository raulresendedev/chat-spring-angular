package com.bradesco.websocket.repository;

import com.bradesco.websocket.bean.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    Long findByNome(String nome);
    List<Grupo> findByIdGrupoIn(List<Long> idGrupo);
    @Query("SELECT g FROM Grupo g WHERE g.idGrupo IN (SELECT gu.idGrupo FROM GrupoUsers gu WHERE gu.username = :username)")
    List<Grupo> obterGruposDoUsuario(@Param("username") String username);
    @Query("SELECT gu.username FROM GrupoUsers gu WHERE gu.idGrupo = :idGrupo")
    List<String> obterUsuariosDoGrupo(@Param("idGrupo") long idGrupo);
}
