package com.agente.pessoal.api;

import com.agente.pessoal.api.dto.MessageDTO;
import com.agente.pessoal.api.dto.SessionDTO;
import com.agente.pessoal.api.dto.SessionDetailDTO;
import com.agente.pessoal.application.SessionService;
import com.agente.pessoal.persistence.entity.ConversationEntity;
import com.agente.pessoal.persistence.entity.MessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para gerenciamento de sessoes (conversas).
 *
 * <p>Expoe endpoints para criar, listar, buscar e deletar conversas.
 * As operacoes de chat ficam em {@link ChatController}.</p>
 */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Cria uma nova sessao vazia.
     *
     * @return sessao criada com status 201
     */
    @PostMapping
    public ResponseEntity<SessionDTO> criarSessao() {
        ConversationEntity conversa = sessionService.criarSessao();
        log.info("Sessao criada via API: {}", conversa.getId());

        SessionDTO dto = SessionDTO.from(conversa, 0);
        return ResponseEntity.status(201).body(dto);
    }

    /**
     * Lista todas as sessoes, mais recentes primeiro.
     *
     * @return lista de sessoes com status 200
     */
    @GetMapping
    public ResponseEntity<List<SessionDTO>> listarSessoes() {
        List<SessionDTO> sessoes = sessionService.listarSessoes().stream()
                .map(conversa -> {
                    int total = sessionService.listarMensagens(conversa.getId()).size();
                    return SessionDTO.from(conversa, total);
                })
                .toList();

        log.debug("Listagem de sessoes retornou {} itens", sessoes.size());
        return ResponseEntity.ok(sessoes);
    }

    /**
     * Retorna uma sessao com todas as suas mensagens.
     *
     * @param id UUID da sessao
     * @return sessao detalhada com status 200, ou 404 se nao existir
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionDetailDTO> buscarSessao(@PathVariable String id) {
        return sessionService.buscarSessao(id)
                .map(conversa -> {
                    List<MessageDTO> mensagens = sessionService.listarMensagens(id).stream()
                            .map(MessageDTO::from)
                            .toList();

                    SessionDetailDTO dto = SessionDetailDTO.from(conversa, mensagens);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("Sessao nao encontrada: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Remove uma sessao e todas as suas mensagens.
     *
     * @param id UUID da sessao
     * @return 204 No Content se removida, 404 se nao existir
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSessao(@PathVariable String id) {
        boolean removida = sessionService.deletarSessao(id);

        if (removida) {
            log.info("Sessao removida via API: {}", id);
            return ResponseEntity.noContent().build();
        }

        log.warn("Tentativa de deletar sessao inexistente via API: {}", id);
        return ResponseEntity.notFound().build();
    }
}