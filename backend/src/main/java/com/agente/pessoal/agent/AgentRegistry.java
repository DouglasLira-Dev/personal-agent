package com.agente.pessoal.agent;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registro central de agentes disponiveis no sistema.
 *
 * <p>O Spring injeta automaticamente todos os beans que estendem
 * {@link BaseAgent}. Este registry os indexa por {@link AgentType},
 * permitindo que o restante da aplicacao busque agentes sem conhecer
 * classes concretas.</p>
 *
 * <p><b>Open/Closed Principle:</b> para adicionar um novo agente, basta
 * criar a classe com {@code @Component} que estende {@code BaseAgent}.
 * Ele entra automaticamente neste registro, sem alterar codigo existente.</p>
 */
@Component
public class AgentRegistry {

    private static final Logger log = LoggerFactory.getLogger(AgentRegistry.class);

    /** Mapa imutavel de AgentType para o agente correspondente. */
    private final Map<AgentType, BaseAgent> agentesPorTipo;

    /**
     * Construtor que recebe todos os agentes injetados pelo Spring
     * e os indexa por tipo.
     *
     * @param agentes lista de todos os beans que estendem {@link BaseAgent}
     */
    public AgentRegistry(List<BaseAgent> agentes) {
        Map<AgentType, BaseAgent> mapa = new EnumMap<>(AgentType.class);

        for (BaseAgent agente : agentes) {
            AgentType tipo = agente.getTipo();

            if (mapa.containsKey(tipo)) {
                throw new IllegalStateException(
                        "Dois agentes registrados para o mesmo AgentType: " + tipo);
            }

            mapa.put(tipo, agente);
        }

        this.agentesPorTipo = Collections.unmodifiableMap(mapa);

        log.info("AgentRegistry inicializado com {} agentes: {}",
                this.agentesPorTipo.size(),
                this.agentesPorTipo.keySet());
    }

    /**
     * Busca o agente correspondente ao tipo informado.
     *
     * @param tipo tipo do agente desejado
     * @return o agente correspondente
     * @throws IllegalArgumentException se nao houver agente para o tipo
     */
    public BaseAgent getAgent(AgentType tipo) {
        BaseAgent agente = agentesPorTipo.get(tipo);
        if (agente == null) {
            throw new IllegalArgumentException(
                    "Nenhum agente registrado para o tipo: " + tipo);
        }
        return agente;
    }

    /**
     * Verifica se existe agente registrado para o tipo informado.
     *
     * @param tipo tipo a verificar
     * @return {@code true} se existir agente para o tipo
     */
    public boolean hasAgent(AgentType tipo) {
        return agentesPorTipo.containsKey(tipo);
    }

    /**
     * Retorna todos os agentes registrados.
     *
     * <p>Util para o front-end listar os modulos disponiveis.</p>
     *
     * @return lista imutavel com todos os agentes
     */
    public List<BaseAgent> getAllAgents() {
        return List.copyOf(agentesPorTipo.values());
    }

    /**
     * Retorna todos os tipos de agentes registrados.
     *
     * @return lista imutavel com os tipos disponiveis
     */
    public List<AgentType> getTiposDisponiveis() {
        return List.copyOf(agentesPorTipo.keySet());
    }

    /**
     * Versao segura de {@link #getAgent(AgentType)} que retorna
     * {@link Optional} em vez de lancar excecao.
     *
     * @param tipo tipo do agente desejado
     * @return {@link Optional} com o agente, ou vazio se nao existir
     */
    public Optional<BaseAgent> buscarAgent(AgentType tipo) {
        return Optional.ofNullable(agentesPorTipo.get(tipo));
    }
}