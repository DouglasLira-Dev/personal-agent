package com.agente.pessoal.exception;

/**
 * Excecao lancada quando uma requisicao contem dados invalidos.
 *
 * <p>Tratada pelo {@link GlobalExceptionHandler} e convertida em 400.</p>
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String mensagem) {
        super(mensagem);
    }

    public InvalidRequestException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}