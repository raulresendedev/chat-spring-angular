package com.bradesco.websocket.controller;

import com.bradesco.websocket.dto.UserDto;
import com.bradesco.websocket.service.AdminClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/api/admin")
public class AdminClientController {

    private final AdminClientService adminClientService;

    @GetMapping("/{username}/{exact}")
    public ResponseEntity<List<UserDto>> teste(@PathVariable("username") String username, @PathVariable("exact") Boolean exact){
        return ResponseEntity.ok(adminClientService.searchByUsername(username, exact));
    }

}
