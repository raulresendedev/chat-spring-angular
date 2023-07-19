package com.bradesco.websocket.controller;

import com.bradesco.websocket.bean.Message;
import com.bradesco.websocket.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/api/message")
public class MessageController {

    private MessageRepository messageRepository;

    @GetMapping("/{idGrupo}/{pagina}")
    public ResponseEntity<Map<String, Object>> getMessagesWithPagination(@PathVariable("idGrupo") Long idGrupo, @PathVariable("pagina") int pagina) {
        Pageable pageable = PageRequest.of(pagina, 25);
        Page<Message> messages = messageRepository.findByIdGrupoOrderByDataDesc(idGrupo, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages.getContent());
        response.put("pagination", messages.getPageable());

        return ResponseEntity.ok(response);
    }
}
