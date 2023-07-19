package com.bradesco.websocket.controller;

import com.bradesco.websocket.dto.GrupoWithUsersDto;
import com.bradesco.websocket.service.GrupoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/api/grupos")
public class GrupoController {

    private GrupoService grupoService;

    @PostMapping
    public ResponseEntity<?> teste(@RequestBody GrupoWithUsersDto grupoDto){
        return grupoService.adicionarGrupo(grupoDto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> obterGrupo(@PathVariable("username") String username){
        return grupoService.obterGrupos(username);
    }

    @GetMapping("/usuarios-do-grupo/{idGrupo}")
    public ResponseEntity<?> obterUsuariosDoGrupo(@PathVariable("idGrupo") long idGrupo){
        return grupoService.obterUsuariosDoGrupo(idGrupo);
    }

    @PutMapping("/{idGrupo}")
    public ResponseEntity<?> editar(@PathVariable("idGrupo") long id, @RequestBody GrupoWithUsersDto grupoDto){
        return grupoService.editarGrupo(id, grupoDto);
    }
}
