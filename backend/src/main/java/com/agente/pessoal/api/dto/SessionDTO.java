package com.agente.pessoal.api.dto;

import com.agente.pessoal.persistence.entity.ConversationEntity;

import java.time.LocalDateTime;

/**
 * DTO resumido de uma sessao (sem as mensagens).
 *
 * <p>Usado na listagem de conversas do front-end.</p>
 *
 * @param id            UUID da conversa
 * @param title         titulo da conversa
 * @param createdAt     momento de criacao
 * @param updatedAt     momento da ultima interacao
 * @param messageCount  quantidade de mensagens na conversa
 */
public record SessionDTO(
        String id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int messageCount
) {
    /**
     * Cria um SessionDTO a partir de uma entidade.
     *
     * @param entidade      entidade ConversationEntity
     * @param messageCount  quantidade de mensagens
     * @return DTO preenchido
     */
    public static SessionDTO from(ConversationEntity entidade, int messageCount) {
        return new SessionDTO(
                entidade.getId(),
                entidade.getTitle(),
                entidade.getCreatedAt(),
                entidade.getUpdatedAt(),
                messageCount
        );
    }
}