package com.bradesco.websocket.service;

import com.bradesco.websocket.bean.Grupo;
import com.bradesco.websocket.bean.GrupoUsers;
import com.bradesco.websocket.bean.NotificacaoUser;
import com.bradesco.websocket.dto.GrupoWithUsersDto;
import com.bradesco.websocket.dto.UserDto;
import com.bradesco.websocket.repository.GrupoRepository;
import com.bradesco.websocket.repository.GrupoUserRepository;
import com.bradesco.websocket.repository.NotificacaoUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final GrupoUserRepository grupoUserRepository;
    private final WebsocketService websocketService;
    private final AdminClientService adminClientService;
    private final NotificacaoUserRepository notificacaoUserRepository;

    public ResponseEntity<?> adicionarGrupo(GrupoWithUsersDto grupoDto){

        Grupo novoGrupo = new Grupo(grupoDto);

        try{
            grupoRepository.save(novoGrupo);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return adicionarUsuariosAoGrupo(novoGrupo.getIdGrupo(), grupoDto.nome(), grupoDto.users());
    }

    public ResponseEntity<?> obterGrupos(String username){
        try{
            return ResponseEntity.ok(grupoRepository.obterGruposDoUsuario(username));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private ResponseEntity<?> adicionarUsuariosAoGrupo(long idGrupo, String nomeGrupo, List<UserDto> users){
        try{

            List<GrupoUsers> listaGrupoUsers = users.stream()
                    .map(user -> {
                        GrupoUsers grupo = new GrupoUsers();
                        grupo.setIdGrupo(idGrupo);
                        grupo.setUsername(user.username());
                        return grupo;
                    })
                    .collect(Collectors.toList());

            grupoUserRepository.saveAll(listaGrupoUsers);



        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        List<NotificacaoUser> listaGrupoUsers = users.stream()
                .map(user -> {
                    NotificacaoUser notificacao = new NotificacaoUser();
                    notificacao.setUsername(user.username());
                    notificacao.setMensagem("Você foi adicionado ao grupo " + nomeGrupo);
                    notificacao.setVisto(false);
                    return notificacao;
                })
                .collect(Collectors.toList());

        notificacaoUserRepository.saveAll(listaGrupoUsers);

        users.stream()
                .map(UserDto::username)
                .forEach(websocketService::notifyGroup);

        listaGrupoUsers
                .forEach(websocketService::enviarNotificacaoUser);

        return ResponseEntity.ok("Grupo Cadastrado");

    }

    public ResponseEntity<?> obterUsuariosDoGrupo(long idGrupo){

        List<String> users = grupoRepository.obterUsuariosDoGrupo(idGrupo);

        List<UserDto> usersDto = users.stream()
                .flatMap(user -> adminClientService.searchByUsername(user, true).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersDto);
    }

    public ResponseEntity<?> editarGrupo(long id, GrupoWithUsersDto grupoDto){

        Grupo grupo = new Grupo(id, grupoDto);

        try{
            grupoRepository.save(grupo);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return editarUsuariosDoGrupo(id, grupo.getNome(), grupoDto.users());
    }

    private ResponseEntity<?> editarUsuariosDoGrupo(long idGrupo, String nomeGrupo, List<UserDto> users){

        List<String> usernames = users.stream()
                .map(UserDto::username)
                .toList();

        List<GrupoUsers> existingUsers = grupoUserRepository.findByIdGrupo(idGrupo);

        List<GrupoUsers> usersToDelete = existingUsers.stream()
                .filter(user -> !usernames.contains(user.getUsername()))
                .collect(Collectors.toList());

        List<NotificacaoUser> notificacoesDelete = usersToDelete.stream()
                .map(user -> {
                    NotificacaoUser notificacao = new NotificacaoUser();
                    notificacao.setUsername(user.getUsername());
                    notificacao.setMensagem("Você foi removido do grupo " + nomeGrupo);
                    notificacao.setVisto(false);
                    return notificacao;
                })
                .toList();

        grupoUserRepository.deleteAll(usersToDelete);

        notificacaoUserRepository.saveAll(notificacoesDelete);

        notificacoesDelete
                .forEach(websocketService::enviarNotificacaoUser);

        usersToDelete.stream()
                .map(GrupoUsers::getUsername)
                .forEach(websocketService::notifyGroup);

        for (String username : usernames) {
            if (existingUsers.stream().noneMatch(user -> user.getUsername().equals(username))) {
                GrupoUsers newUser = new GrupoUsers();
                newUser.setIdGrupo(idGrupo);
                newUser.setUsername(username);
                grupoUserRepository.save(newUser);

                NotificacaoUser notificacao = new NotificacaoUser();
                notificacao.setUsername(username);
                notificacao.setMensagem("Você foi adicionado ao grupo " + nomeGrupo);
                notificacao.setVisto(false);

                notificacaoUserRepository.save(notificacao);

                websocketService.enviarNotificacaoUser(notificacao);
            }
        }

        users.stream()
                .map(UserDto::username)
                .forEach(websocketService::notifyGroup);

        return ResponseEntity.ok(existingUsers);
    }
}
