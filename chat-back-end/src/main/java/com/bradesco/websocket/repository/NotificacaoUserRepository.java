package com.bradesco.websocket.repository;

import com.bradesco.websocket.bean.NotificacaoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificacaoUserRepository extends JpaRepository<NotificacaoUser, Long> {

    @Query("SELECT n FROM NotificacaoUser n WHERE n.username = :username ORDER BY n.data desc")
    List<NotificacaoUser> obterNotificacaoDoUsuario(@Param("username") String username);

    void deleteById(long idNotificacao);
}
