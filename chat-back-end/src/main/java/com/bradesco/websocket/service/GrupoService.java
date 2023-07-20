package com.bradesco.websocket.service;

import com.bradesco.websocket.bean.Grupo;
import com.bradesco.websocket.bean.GrupoUsers;
import com.bradesco.websocket.bean.NotificacaoUser;
import com.bradesco.websocket.dto.GrupoWithUsersDto;
import com.bradesco.websocket.dto.UserDto;
import com.bradesco.websocket.repository.GrupoRepository;
import com.bradesco.websocket.repository.GrupoUserRepository;
import com.bradesco.websocket.repository.NotificacaoUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
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
                    notificacao.setData(new Date());
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

    @Transactional
    public ResponseEntity<?> removerUserDoGrupo(long idGrupo, String username){
        grupoUserRepository.deleteByUsernameAndIdGrupo(username, idGrupo);
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
                        GrupoUsers newUser = new GrupoUsers();
                        newUser.setIdGrupo(id);
                        newUser.setUsername(username);
                        return newUser;
                    })
                    .toList();

            List<GrupoUsers> usersToDelete = existingUsers.stream()
                    .filter(user -> !usernames.contains(user.getUsername()))
                    .toList();

            grupoUserRepository.saveAll(usersToAdd);

            grupoUserRepository.deleteAll(usersToDelete);

            notificoesCriarEnviar(usersToAdd, "Você foi adicionado ao grupo " + grupo.getNome());

            notificoesCriarEnviar(usersToDelete, "Você foi removido do grupo " + grupo.getNome());

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

    private void notificoesCriarEnviar(List<GrupoUsers> usuarios, String mensagem){

        List<NotificacaoUser> notificacoes = usuarios.stream()
                .map(user -> {
                    NotificacaoUser notificacao = new NotificacaoUser();
                    notificacao.setUsername(user.getUsername());
                    notificacao.setMensagem(mensagem);
                    notificacao.setVisto(false);
                    notificacao.setData(new Date());
                    return notificacao;
                })
                .toList();

        notificacaoUserRepository.saveAll(notificacoes);

        notificacoes
                .forEach(websocketService::enviarNotificacaoUser);
    }

}
