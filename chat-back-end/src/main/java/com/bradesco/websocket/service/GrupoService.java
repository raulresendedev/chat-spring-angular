package com.bradesco.websocket.service;

import com.bradesco.websocket.bean.Grupo;
import com.bradesco.websocket.bean.GrupoUsers;
import com.bradesco.websocket.bean.Message;
import com.bradesco.websocket.bean.NotificacaoUser;
import com.bradesco.websocket.dto.GrupoWithUsersDto;
import com.bradesco.websocket.dto.UserDto;
import com.bradesco.websocket.repository.GrupoRepository;
import com.bradesco.websocket.repository.GrupoUserRepository;
import com.bradesco.websocket.repository.MessageRepository;
import com.bradesco.websocket.repository.NotificacaoUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
    private final MessageRepository messageRepository;

    public ResponseEntity<?> adicionarGrupo(GrupoWithUsersDto grupoDto){

        Grupo novoGrupo = new Grupo(grupoDto);

        try{
            infoMessagecriarGrupo(novoGrupo, grupoDto.usuarioAcao());
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return adicionarUsuariosAoGrupo(novoGrupo.getIdGrupo(), grupoDto.nome(), grupoDto.usuarioAcao(), grupoDto.users());
    }

    public ResponseEntity<?> obterGrupos(String username){
        try{
            return ResponseEntity.ok(grupoRepository.obterGruposDoUsuario(username));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Transactional
    private ResponseEntity<?> adicionarUsuariosAoGrupo(long idGrupo, String nomeGrupo, String usuarioAcao, List<UserDto> users){
        try{

            List<GrupoUsers> listaGrupoUsers = users.stream()
                    .map(user -> {
                        return new GrupoUsers(idGrupo, user.username());
                    })
                    .collect(Collectors.toList());

            grupoUserRepository.saveAll(listaGrupoUsers);

            listaGrupoUsers.removeIf(x -> x.getUsername().equals(usuarioAcao));

            infoMessagesAdicionarUsuarios(idGrupo, listaGrupoUsers, usuarioAcao);

            notificacoesAdionarUsuarios(listaGrupoUsers, usuarioAcao,  nomeGrupo);

            users.stream()
                    .map(UserDto::username)
                    .forEach(websocketService::notifyGroup);

            return ResponseEntity.ok("Grupo Cadastrado");

        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    @Transactional
    public ResponseEntity<?> sairDoGrupo(long idGrupo, String username){

        grupoUserRepository.deleteByUsernameAndIdGrupo(username, idGrupo);

        infoMessageSairDoGrupo(idGrupo, username);

        return ResponseEntity.ok("Saiu do Grupo");
    }

    public ResponseEntity<?> obterUsuariosDoGrupo(long idGrupo){

        List<String> users = grupoRepository.obterUsuariosDoGrupo(idGrupo);

        List<UserDto> usersDto = users.stream()
                .flatMap(user -> adminClientService.searchByUsername(user, true).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(usersDto);
    }

    @Transactional
    public ResponseEntity<?> editarGrupo(long id, GrupoWithUsersDto grupoDto){
        try {
            Grupo grupo = new Grupo(id, grupoDto);
            grupoRepository.save(grupo);

            List<String> usernames = grupoDto.users().stream()
                    .map(UserDto::username)
                    .toList();

            List<GrupoUsers> existingUsers = grupoUserRepository.findByIdGrupo(id);

            List<GrupoUsers> usersToAdd = usernames.stream()
                    .filter(username -> existingUsers.stream().noneMatch(user -> user.getUsername().equals(username)))
                    .map(username -> {
                        return new GrupoUsers(id, username);
                    })
                    .toList();

            List<GrupoUsers> usersToDelete = existingUsers.stream()
                    .filter(user -> !usernames.contains(user.getUsername()))
                    .toList();

            grupoUserRepository.saveAll(usersToAdd);

            grupoUserRepository.deleteAll(usersToDelete);

            infoMessagesAdicionarUsuarios(id, usersToAdd, grupoDto.usuarioAcao());

            infoMessagesRemoverUsuarios(id, usersToDelete, grupoDto.usuarioAcao());

            notificacoesAdionarUsuarios(usersToAdd, grupoDto.usuarioAcao(),  grupo.getNome());

            notificacoesRemoverUsuarios(usersToDelete, grupoDto.usuarioAcao(), grupo.getNome());

            usersToDelete.stream()
                    .map(GrupoUsers::getUsername)
                    .forEach(websocketService::notifyGroup);

            grupoDto.users().stream()
                    .map(UserDto::username)
                    .forEach(websocketService::notifyGroup);

            return ResponseEntity.ok(existingUsers);

        }catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.internalServerError().body("Erro ao editar grupo");
        }
    }

    private void infoMessagecriarGrupo(Grupo novoGrupo, String usuarioAcao){

        grupoRepository.save(novoGrupo);

        Message mensagem = new Message(novoGrupo.getIdGrupo(),usuarioAcao + " criou o grupo");

        messageRepository.save(mensagem);

        websocketService.notifyGroupInfo(mensagem);
    }

    private void infoMessageSairDoGrupo(long idGrupo, String usuario){

        Message mensagem = new Message(idGrupo,usuario + " saiu do grupo");

        messageRepository.save(mensagem);

        websocketService.notifyGroupInfo(mensagem);
    }

    private void infoMessagesAdicionarUsuarios(long grupoId, List<GrupoUsers> usuariosAdicionados, String usuarioAcao){

        List<Message> addInfoMessages = usuariosAdicionados.stream()
                .map(userioAdicionado -> {
                    return new Message(grupoId, usuarioAcao + " adicionou " + userioAdicionado.getUsername() + " ao grupo");
                }).toList();

        messageRepository.saveAll(addInfoMessages);

        addInfoMessages.forEach(websocketService::notifyGroupInfo);

    }

    private void infoMessagesRemoverUsuarios(long grupoId, List<GrupoUsers> usuariosRemovidos, String usuarioAcao){

        List<Message> deleteInfoMessages = usuariosRemovidos.stream()
                .map(userioRemovido -> {
                    return new Message(grupoId, usuarioAcao + " removeu " + userioRemovido.getUsername() + " do grupo");
                }).toList();

        messageRepository.saveAll(deleteInfoMessages);

        deleteInfoMessages.forEach(websocketService::notifyGroupInfo);

    }

    private void notificacoesAdionarUsuarios(List<GrupoUsers> usuariosAdicionados, String usuarioAcao, String nomeGrupo){

        List<NotificacaoUser> notificacoes = usuariosAdicionados.stream()
                .map(usuarioAdicionado -> {
                    return new NotificacaoUser(usuarioAdicionado.getUsername(), usuarioAcao + " te adicionou ao grupo " + nomeGrupo);
                })
                .toList();

        notificacaoUserRepository.saveAll(notificacoes);

        notificacoes
                .forEach(websocketService::enviarNotificacaoUser);
    }

    private void notificacoesRemoverUsuarios(List<GrupoUsers> usuariosRemovidos, String usuarioAcao, String nomeGrupo){

        List<NotificacaoUser> notificacoes = usuariosRemovidos.stream()
                .map(usuarioRemovido -> {
                    return new NotificacaoUser(usuarioRemovido.getUsername(), usuarioAcao + " te removeu do grupo " + nomeGrupo);
                })
                .toList();

        notificacaoUserRepository.saveAll(notificacoes);

        notificacoes
                .forEach(websocketService::enviarNotificacaoUser);
    }

}
