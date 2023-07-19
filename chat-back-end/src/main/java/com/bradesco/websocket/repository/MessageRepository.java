package com.bradesco.websocket.repository;

import com.bradesco.websocket.bean.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByIdGrupoOrderByDataDesc(Long grupoId, Pageable pageable);
}
