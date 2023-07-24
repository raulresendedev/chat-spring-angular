package com.bradesco.websocket.dto;

import java.util.List;

public record GrupoWithUsersDto(String nome, String usuarioAcao, List<UserDto> users) {
}
