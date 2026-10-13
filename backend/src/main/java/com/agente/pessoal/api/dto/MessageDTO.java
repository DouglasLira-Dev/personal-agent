package com.agente.pessoal.api.dto;

import com.agente.pessoal.persistence.entity.MessageEntity;

import java.time.LocalDateTime;

/**
 * DTO de mensagem individual de uma conversa.
 *
 * @param role      papel do autor (USER ou ASSISTANT)
 * @param content   conteudo textual
 * @param agentType tipo do agente que respondeu (pode ser nulo)
 * @param timestamp momento em que a mensagem foi enviada
 */
public record MessageDTO(
        String role,
        String content,
        String agentType,
        LocalDateTime timestamp
) {
    /**
     * Cria um MessageDTO a partir de uma entidade.
     *
     * @param entidade entidade MessageEntity
     * @return DTO preenchido
     */
    public static MessageDTO from(MessageEntity entidade) {
        return new MessageDTO(
                entidade.getRole(),
                entidade.getContent(),
                entidade.getAgentType(),
                entidade.getTimestamp()
        );
    }
}