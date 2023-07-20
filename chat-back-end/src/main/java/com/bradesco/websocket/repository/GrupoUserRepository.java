package com.bradesco.websocket.repository;

import com.bradesco.websocket.bean.GrupoUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrupoUserRepository extends JpaRepository<GrupoUsers, Long> {

    void deleteByUsernameAndIdGrupo(String username, long idGrupo);
    List<GrupoUsers> findByIdGrupo(long idGrupo);
}
