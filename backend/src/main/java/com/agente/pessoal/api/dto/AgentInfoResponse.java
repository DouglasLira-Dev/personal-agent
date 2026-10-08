package com.agente.pessoal.api.dto;

import com.agente.pessoal.domain.AgentType;

/**
 * DTO de informacao de um agente disponivel.
 *
 * <p>Usado pelo endpoint GET /api/agents para o front montar
 * o seletor de modulos.</p>
 *
 * @param type        codigo do tipo do agente (ex: EXCEL)
 * @param description descricao legivel (ex: Especialista em Excel)
 */
public record AgentInfoResponse(
        String type,
        String description
) {
    /**
     * Cria um AgentInfoResponse a partir de um AgentType.
     *
     * @param tipo tipo do agente
     * @return DTO preenchido
     */
    public static AgentInfoResponse from(AgentType tipo) {
        return new AgentInfoResponse(tipo.getCodigo(), tipo.getDescricao());
    }
}