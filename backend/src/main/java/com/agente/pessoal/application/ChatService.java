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
 * <p><b>Fluxo:</b>
 * <ol>
 *   <li>Recebe o ChatRequest (sessionId + message)</li>
 *   <li>Se sessionId vazio, cria nova sessao via SessionService</li>
 *   <li>Roteia (ou usa agent forcado)</li>
 *   <li>Salva a mensagem do usuario no banco</li>
 *   <li>Chama o agente</li>
 *   <li>Salva a resposta do agente no banco</li>
 *   <li>Atualiza o titulo se for a primeira mensagem</li>
 *   <li>Retorna o ChatResponse</li>
 * </ol>
 * </p>
 *
 * <p><b>Privacidade:</b> o conteudo da mensagem NUNCA e logado. Apenas
 * metadados (sessionId, tamanho, agente escolhido).</p>
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    /** Papel do usuario nas mensagens persistidas. */
    private static final String ROLE_USER = "USER";

    /** Papel do assistente nas mensagens persistidas. */
    private static final String ROLE_ASSISTANT = "ASSISTANT";

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
    private final SessionService sessionService;

    public ChatService(RouterService routerService,
                       AgentRegistry agentRegistry,
                       SessionService sessionService) {
        this.routerService = routerService;
        this.agentRegistry = agentRegistry;
        this.sessionService = sessionService;
    }

    /**
     * Processa uma requisicao de chat e retorna a resposta do agente.
     *
     * @param requisicao dados da mensagem enviada pelo usuario
     * @return resposta do agente com metadados
     */
    public ChatResponse chat(ChatRequest requisicao) {
        // Passo 1: garante uma sessao valida
        String sessionId = garantirSessao(requisicao.sessionId());

        int tamanhoMensagem = requisicao.message() != null ? requisicao.message().length() : 0;
        log.info("Chat recebido — sessionId: {}, tamanho da mensagem: {}",
                sessionId, tamanhoMensagem);

        long inicioTotal = System.currentTimeMillis();

        // Passo 2: salva mensagem do usuario ANTES de qualquer chamada ao LLM
        sessionService.salvarMensagem(
                sessionId, ROLE_USER, requisicao.message(), null);

        // Passo 3: atualiza o titulo se for a primeira mensagem
        sessionService.atualizarTituloSeNecessario(sessionId, requisicao.message());

        // Passo 4: roteia
        AgentType tipoEscolhido = resolverTipo(requisicao);

        // Passo 5: trata caso UNCERTAIN
        if (tipoEscolhido == AgentType.UNCERTAIN) {
            log.warn("Chat finalizado com UNCERTAIN — sessionId: {}", sessionId);

            sessionService.salvarMensagem(
                    sessionId, ROLE_ASSISTANT, MENSAGEM_ESCLARECIMENTO, AgentType.UNCERTAIN);

            return new ChatResponse(
                    sessionId,
                    AgentType.UNCERTAIN,
                    MENSAGEM_ESCLARECIMENTO,
                    LocalDateTime.now(),
                    true
            );
        }

        // Passo 6: chama o agente
        BaseAgent agente = agentRegistry.getAgent(tipoEscolhido);

        long inicioAgente = System.currentTimeMillis();
        String resposta = agente.chat(requisicao.message(), sessionId);
        long duracaoAgente = System.currentTimeMillis() - inicioAgente;

        // Passo 7: salva resposta do agente
        sessionService.salvarMensagem(
                sessionId, ROLE_ASSISTANT, resposta, tipoEscolhido);

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
     * @return lista de modulos disponiveis
     */
    public List<AgentInfoResponse> listarAgentesDisponiveis() {
        return agentRegistry.getAllAgents().stream()
                .map(agente -> AgentInfoResponse.from(agente.getTipo()))
                .toList();
    }

    /**
     * Garante que existe uma sessao valida.
     *
     * <p>Se o sessionId vier vazio, cria uma nova sessao. Se vier preenchido
     * mas a sessao nao existir, cria com o ID informado (compatibilidade
     * com sessoes antigas ou geradas fora deste servico).</p>
     *
     * @param sessionId sessionId recebido na requisicao (pode ser nulo/vazio)
     * @return o sessionId valido (novo ou existente)
     */
    private String garantirSessao(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            String novoId = sessionService.criarSessao().getId();
            log.info("Sessao criada automaticamente: {}", novoId);
            return novoId;
        }

        if (sessionService.buscarSessao(sessionId).isEmpty()) {
            sessionService.criarSessaoComId(sessionId);
            log.info("Sessao criada com ID informado: {}", sessionId);
        }

        return sessionId;
    }

    /**
     * Resolve qual {@link AgentType} deve responder.
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
}