package com.agente.pessoal.api.dto;

import java.time.LocalDateTime;

/**
 * DTO padronizado de resposta de erro.
 *
 * @param timestamp momento em que o erro ocorreu
 * @param status    codigo HTTP
 * @param error     mensagem curta (ex: "Not Found")
 * @param message   detalhes do erro
 * @param path      endpoint chamado
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}