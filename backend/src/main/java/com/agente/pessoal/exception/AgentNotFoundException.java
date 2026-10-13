package com.agente.pessoal.exception;

/**
 * Excecao lancada quando um agente/modulo solicitado nao existe.
 *
 * <p>Tratada pelo {@link GlobalExceptionHandler} e convertida em 404.</p>
 */
public class AgentNotFoundException extends RuntimeException {

    private final String codigoAgente;

    public AgentNotFoundException(String codigoAgente) {
        super("Agente nao encontrado: " + codigoAgente);
        this.codigoAgente = codigoAgente;
    }

    public String getCodigoAgente() {
        return codigoAgente;
    }
}