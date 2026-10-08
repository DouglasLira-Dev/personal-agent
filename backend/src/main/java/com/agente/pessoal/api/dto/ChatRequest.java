package com.agente.pessoal.api.dto;

import com.agente.pessoal.domain.AgentType;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de requisicao do endpoint de chat.
 *
 * <p>Carrega a mensagem do usuario e o identificador da sessao usada
 * para memoria de conversa.</p>
 *
 * @param sessionId identificador da sessao da conversa (opcional)
 * @param message   mensagem enviada pelo usuario (obrigatoria)
 * @param agent     tipo de agente a forcar (opcional - se null, o roteador decide)
 */
public record ChatRequest(

    String sessionId,

    @NotBlank (message = "A mensagem e obrigatoria")
    String message,

    AgentType agent
) {

}