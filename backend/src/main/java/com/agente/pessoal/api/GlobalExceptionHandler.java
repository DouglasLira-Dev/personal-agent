package com.agente.pessoal.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Handler global de excecoes da API.
 *
 * <p>Converte excecoes nao tratadas em respostas HTTP apropriadas.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata IllegalArgumentException (ex: AgentType invalido) como 400.
     *
     * @param e excecao capturada
     * @return resposta 400 com mensagem
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarArgumentoInvalido(IllegalArgumentException e) {
        log.warn("Requisicao invalida: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "erro", "Requisicao invalida",
                        "mensagem", e.getMessage()
                ));
    }
}