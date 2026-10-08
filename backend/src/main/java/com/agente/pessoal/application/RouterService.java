package com.agente.pessoal.application;

import com.agente.pessoal.agent.RouterAgent;
import com.agente.pessoal.domain.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servico de roteamento de mensagens.
 *
 * <p>Encapsula a logica de classificacao de intencao, aplicando regras de
 * negocio em cima da resposta do {@link RouterAgent}: tratamento de erros,
 * fallback para {@link AgentType#UNCERTAIN} e logging das decisoes.</p>
 *
 * <p><b>Separacao de responsabilidades:</b> o {@code RouterAgent} apenas
 * chama o LLM; este servico aplica as regras e trata excecoes.</p>
 */
@Service
public class RouterService {

    private static final Logger log = LoggerFactory.getLogger(RouterService.class);

    /** Agente responsavel por classificar a mensagem via LLM. */
    private final RouterAgent routerAgent;

    /**
     * Construtor com injecao do RouterAgent.
     *
     * @param routerAgent agente roteador
     */
    public RouterService(RouterAgent routerAgent) {
        this.routerAgent = routerAgent;
    }

    /**
     * Classifica a mensagem em um unico {@link AgentType}.
     *
     * <p>Se a classificacao falhar ou retornar {@link AgentType#UNCERTAIN},
     * o valor {@code UNCERTAIN} e retornado como fallback seguro.</p>
     *
     * @param mensagem mensagem do usuario
     * @return o tipo do agente mais adequado, ou {@code UNCERTAIN}
     */
    public AgentType route(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            log.warn("RouterService recebeu mensagem vazia — retornando UNCERTAIN");
            return AgentType.UNCERTAIN;
        }

        try {
            AgentType tipo = routerAgent.classificar(mensagem);

            if (tipo == null) {
                log.warn("RouterAgent retornou null — tratando como UNCERTAIN");
                return AgentType.UNCERTAIN;
            }

            log.info("Mensagem classificada como: {} ({})", tipo, tipo.getDescricao());
            return tipo;

        } catch (Exception e) {
            log.error("Falha ao classificar mensagem — retornando UNCERTAIN. Causa: {}",
                    e.getMessage());
            return AgentType.UNCERTAIN;
        }
    }

    /**
     * Classifica a mensagem em uma lista de {@link AgentType}.
     *
     * <p>Preparado para o encadeamento futuro (Fase 5), quando uma mensagem
     * pode exigir multiplos agentes em sequencia. Por enquanto, retorna
     * uma lista com um unico elemento (ou lista vazia se UNCERTAIN).</p>
     *
     * @param mensagem mensagem do usuario
     * @return lista de tipos de agentes (vazia se nao foi possivel classificar)
     */
    public List<AgentType> routeMultiple(String mensagem) {
        AgentType tipo = route(mensagem);

        if (tipo == AgentType.UNCERTAIN) {
            return List.of();
        }

        return List.of(tipo);
    }
}