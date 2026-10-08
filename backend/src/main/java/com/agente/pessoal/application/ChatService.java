package com.agente.pessoal.application;

import com.agente.pessoal.agent.AgentRegistry;
import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.api.dto.AgentInfoResponse;
import com.agente.pessoal.api.dto.ChatRequest;
import com.agente.pessoal.api.dto.ChatResponse;
import com.agente.pessoal.domain.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
 *
 * <p><b>Privacidade:</b> o conteudo da mensagem NUNCA e logado. Apenas
 * metadados (sessionId, tamanho, agente escolhido).</p>
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
        int tamanhoMensagem = requisicao.message() != null ? requisicao.message().length() : 0;

        log.info("Chat recebido — sessionId: {}, tamanho da mensagem: {}",
                sessionId, tamanhoMensagem);

        long inicioTotal = System.currentTimeMillis();

        AgentType tipoEscolhido = resolverTipo(requisicao);

        if (tipoEscolhido == AgentType.UNCERTAIN) {
            log.warn("Chat finalizado com UNCERTAIN — sessionId: {} — solicitando esclarecimento",
                    sessionId);
            return new ChatResponse(
                    sessionId,
                    AgentType.UNCERTAIN,
                    MENSAGEM_ESCLARECIMENTO,
                    LocalDateTime.now(),
                    true
            );
        }

        BaseAgent agente = agentRegistry.getAgent(tipoEscolhido);

        long inicioAgente = System.currentTimeMillis();
        String resposta = agente.chat(requisicao.message(), sessionId);
        long duracaoAgente = System.currentTimeMillis() - inicioAgente;

        long duracaoTotal = System.currentTimeMillis() - inicioTotal;

        log.info("Chat respondido — sessionId: {}, agente: {}, tempo do agente: {} ms, tempo total: {} ms",
                sessionId, tipoEscolhido, duracaoAgente, duracaoTotal);

        return new ChatResponse(
                sessionId,
                tipoEscolhido,
                resposta,
                LocalDateTime.now(),
                false
        );
    }

    /**
     * Lista os agentes disponiveis para o front-end.
     *
     * <p>Exclui o tipo especial {@link AgentType#UNCERTAIN}.</p>
     *
     * @return lista de modulos disponiveis
     */
    public List<AgentInfoResponse> listarAgentesDisponiveis() {
        return agentRegistry.getAllAgents().stream()
                .map(agente -> AgentInfoResponse.from(agente.getTipo()))
                .toList();
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
            log.info("Agente forcado no request: {}", requisicao.agent());
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