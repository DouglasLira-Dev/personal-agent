package com.agente.pessoal.exception;

/**
 * Excecao lancada quando uma sessao nao existe.
 *
 * <p>Tratada pelo {@link GlobalExceptionHandler} e convertida em 404.</p>
 */
public class SessionNotFoundException extends RuntimeException {

    private final String sessionId;

    public SessionNotFoundException(String sessionId) {
        super("Sessao nao encontrada: " + sessionId);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}