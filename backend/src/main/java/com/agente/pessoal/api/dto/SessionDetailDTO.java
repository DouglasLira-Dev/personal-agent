package com.agente.pessoal.api.dto;

import com.agente.pessoal.persistence.entity.ConversationEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO detalhado de uma sessao, incluindo todas as mensagens.
 *
 * <p>Usado quando o front-end abre uma conversa especifica.</p>
 *
 * @param id         UUID da conversa
 * @param title      titulo da conversa
 * @param createdAt  momento de criacao
 * @param updatedAt  momento da ultima interacao
 * @param messages   lista de mensagens da conversa
 */
public record SessionDetailDTO(
        String id,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MessageDTO> messages
) {
    /**
     * Cria um SessionDetailDTO a partir de uma entidade e suas mensagens.
     *
     * @param entidade entidade ConversationEntity
     * @param messages lista de MessageDTO
     * @return DTO preenchido
     */
    public static SessionDetailDTO from(ConversationEntity entidade, List<MessageDTO> messages) {
        return new SessionDetailDTO(
                entidade.getId(),
                entidade.getTitle(),
                entidade.getCreatedAt(),
                entidade.getUpdatedAt(),
                messages
        );
    }
}