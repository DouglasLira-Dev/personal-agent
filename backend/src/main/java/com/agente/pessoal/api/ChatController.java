package com.agente.pessoal.api;

import com.agente.pessoal.api.dto.AgentInfoResponse;
import com.agente.pessoal.api.dto.ChatRequest;
import com.agente.pessoal.api.dto.ChatResponse;
import com.agente.pessoal.application.ChatService;
import com.agente.pessoal.domain.AgentType;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST do chat.
 *
 * <p>Expoe endpoints para conversar com o agente (roteamento automatico ou
 * manual) e para listar os modulos disponiveis.</p>
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Endpoint principal: roteamento automatico ou override via body.
     *
     * @param requisicao dados da mensagem (agent opcional no body)
     * @return resposta do agente com status 200
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest requisicao) {
        ChatResponse resposta = chatService.chat(requisicao);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Endpoint com modulo forcado via path param.
     *
     * <p>O path param tem prioridade sobre o campo agent do body.</p>
     *
     * @param agent      codigo do modulo (ex: EXCEL, GIT)
     * @param requisicao dados da mensagem
     * @return resposta do agente com status 200
     */
    @PostMapping("/chat/{agent}")
    public ResponseEntity<ChatResponse> chatComAgente(
            @PathVariable String agent,
            @Valid @RequestBody ChatRequest requisicao) {

        AgentType tipo = converterAgent(agent);

        // Path param tem prioridade sobre o body
        ChatRequest requisicaoComAgente = new ChatRequest(
                requisicao.sessionId(),
                requisicao.message(),
                tipo
        );

        log.info("Endpoint /chat/{} — agente forcado via path", tipo);
        ChatResponse resposta = chatService.chat(requisicaoComAgente);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Lista os modulos disponiveis.
     *
     * @return lista de modulos com type e description
     */
    @GetMapping("/agents")
    public ResponseEntity<List<AgentInfoResponse>> listarAgentes() {
        List<AgentInfoResponse> agentes = chatService.listarAgentesDisponiveis();
        return ResponseEntity.ok(agentes);
    }

    /**
     * Converte o path param em AgentType, lancando excecao se invalido.
     *
     * @param agent codigo do modulo
     * @return AgentType correspondente
     * @throws IllegalArgumentException se o codigo nao corresponder a um agente real
     */
    private AgentType converterAgent(String agent) {
        AgentType tipo = AgentType.porCodigo(agent);

        if (!tipo.isAgenteReal()) {
            throw new IllegalArgumentException(
                    "Nao e possivel forcar o tipo especial: " + agent);
        }

        return tipo;
    }
}