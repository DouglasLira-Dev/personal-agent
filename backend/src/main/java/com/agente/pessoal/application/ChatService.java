package com.agente.pessoal.application;

import com.agente.pessoal.agent.AgentRegistry;
import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.api.dto.ChatRequest;
import com.agente.pessoal.api.dto.ChatResponse;
import com.agente.pessoal.domain.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servico de orquestracao do chat.
 *
 * <p>Fluxo:
 * <ol>
 *   <li>Se o request tem agente explicito, usa ele</li>
 *   <li>Caso contrario, consulta o {@link RouterService}</li>
 *   <li>Se o roteador retornar {@link AgentType#UNCERTAIN}, pede esclarecimento</li>
 *   <li>Caso contrario, busca o agente no {@link AgentRegistry} e chama</li>
 * </ol>
 * </p>
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    /** Mensagem padrao quando o roteador nao consegue classificar. */
    private static final String MENSAGEM_ESCLARECIMENTO = """
            Nao consegui identificar com clareza qual modulo deve responder sua pergunta.

            Voce pode reformular ou indicar explicitamente o modulo desejado:
            - EXCEL: formulas, planilhas, VBA
            - LGPD: dados pessoais, privacidade
            - DOC: README, documentacao de API
            - LOG: erros, stack traces
            - SEC: seguranca de codigo, vulnerabilidades
            - GIT: branches, commits, pull requests
            """;

    private final RouterService routerService;
    private final AgentRegistry agentRegistry;

    /**
     * Construtor com injecao das dependencias.
     *
     * @param routerService servico de roteamento
     * @param agentRegistry registro central de agentes
     */
    public ChatService(RouterService routerService, AgentRegistry agentRegistry) {
        this.routerService = routerService;
        this.agentRegistry = agentRegistry;
    }

    /**
     * Processa uma requisicao de chat e retorna a resposta do agente.
     *
     * @param requisicao dados da mensagem enviada pelo usuario
     * @return resposta do agente com metadados
     */
    public ChatResponse chat(ChatRequest requisicao) {
        String sessionId = normalizarSessionId(requisicao.sessionId());

        // Passo 1 e 2: agente explicito tem prioridade
        AgentType tipoEscolhido = resolverTipo(requisicao);

        // Passo 3: roteador nao conseguiu classificar
        if (tipoEscolhido == AgentType.UNCERTAIN) {
            log.info("Roteador retornou UNCERTAIN — pedindo esclarecimento ao usuario");
            return new ChatResponse(
                    sessionId,
                    AgentType.UNCERTAIN,
                    MENSAGEM_ESCLARECIMENTO,
                    LocalDateTime.now(),
                    true
            );
        }

        // Passo 4 e 5: busca o agente e chama
        BaseAgent agente = agentRegistry.getAgent(tipoEscolhido);

        log.info("Encaminhando mensagem para agente: {}", tipoEscolhido);
        String resposta = agente.chat(requisicao.message(), sessionId);

        return new ChatResponse(
                sessionId,
                tipoEscolhido,
                resposta,
                LocalDateTime.now(),
                false
        );
    }

    /**
     * Resolve qual {@link AgentType} deve responder.
     *
     * <p>Se o request tem agente explicito, respeita. Caso contrario,
     * consulta o roteador.</p>
     *
     * @param requisicao requisicao do usuario
     * @return tipo do agente a usar (pode ser UNCERTAIN)
     */
    private AgentType resolverTipo(ChatRequest requisicao) {
        if (requisicao.agent() != null && requisicao.agent().isAgenteReal()) {
            log.info("Agente explicito informado no request: {}", requisicao.agent());
            return requisicao.agent();
        }

        return routerService.route(requisicao.message());
    }

    /**
     * Garante que o sessionId nunca seja nulo ou vazio.
     *
     * @param sessionId identificador recebido na requisicao
     * @return sessionId valido (o original ou um UUID novo)
     */
    private String normalizarSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return sessionId;
    }
}