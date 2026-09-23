package com.agente.pessoal.api.dto;

import com.agente.pessoal.domain.AgentType;

import java.time.LocalDateTime;

/**
 * DTO de resposta do endpoint de chat
 * 
 * <p>Retorna a resposta gerada pelo agente, junto com metadados uteis
 * para o front-end e para depuracao.</p>
 * 
 * @param sessionId eco do identificador da sessao
 * @param agentUsed tipo do agente que respondeu
 * @param response  texto gerado pelo agente
 * @param timestamp momento em que a resposta foi gerada
 */
public record ChatResponse(
        String sessionId,
        AgentType agentUsed,
        String response,
        LocalDateTime timestamp
) {
    
}