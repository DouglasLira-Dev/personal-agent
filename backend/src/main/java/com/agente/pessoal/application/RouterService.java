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
 * <p><b>Privacidade:</b> o conteudo da mensagem NUNCA e logado. Apenas
 * metadados (tamanho, sessionId, tipo escolhido).</p>
 */
@Service
public class RouterService {

    private static final Logger log = LoggerFactory.getLogger(RouterService.class);

    private final RouterAgent routerAgent;

    public RouterService(RouterAgent routerAgent) {
        this.routerAgent = routerAgent;
    }

    /**
     * Classifica a mensagem em um unico {@link AgentType}.
     *
     * @param mensagem mensagem do usuario
     * @return o tipo do agente mais adequado, ou {@code UNCERTAIN}
     */
    public AgentType route(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            log.warn("Roteamento recebeu mensagem vazia — retornando UNCERTAIN");
            return AgentType.UNCERTAIN;
        }

        long inicio = System.currentTimeMillis();

        try {
            AgentType tipo = routerAgent.classificar(mensagem);
            long duracao = System.currentTimeMillis() - inicio;

            if (tipo == null || tipo == AgentType.UNCERTAIN) {
                log.warn("Roteador retornou UNCERTAIN em {} ms (tamanho da mensagem: {})",
                        duracao, mensagem.length());
                return AgentType.UNCERTAIN;
            }

            log.info("Roteamento decidido: {} ({}) em {} ms — tamanho da mensagem: {}",
                    tipo, tipo.getDescricao(), duracao, mensagem.length());

            return tipo;

        } catch (Exception e) {
            long duracao = System.currentTimeMillis() - inicio;
            log.error("Erro ao rotear mensagem apos {} ms — causa: {}",
                    duracao, e.getMessage(), e);
            return AgentType.UNCERTAIN;
        }
    }

    /**
     * Classifica a mensagem em uma lista de {@link AgentType}.
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