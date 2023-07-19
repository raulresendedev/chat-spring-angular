package com.bradesco.websocket.service;

import com.bradesco.websocket.dto.UserDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminClientService {

    @Autowired
    Keycloak keycloak;

    public List<UserDto> searchByUsername(String username, boolean exact) {
        List<UserRepresentation> users = keycloak.realm("websocket")
                .users()
                .searchByUsername(username, exact);

        List<UserDto> userDtos = new ArrayList<>();

        for (UserRepresentation user : users) {
            UserDto dto = new UserDto(
                    (user.getFirstName() + " " + user.getLastName()).equals(" ") ? "Usuario" : user.getFirstName() + " " + user.getLastName(),
                    user.getEmail(),
                    user.getUsername());
            userDtos.add(dto);
        }

        return userDtos;
    }
}