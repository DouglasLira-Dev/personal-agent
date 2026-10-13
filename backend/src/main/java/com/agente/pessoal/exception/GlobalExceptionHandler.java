package com.agente.pessoal.exception;

import com.agente.pessoal.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Handler global de excecoes da API.
 *
 * <p>Padroniza todas as respostas de erro em um formato consistente:</p>
 * <pre>
 * {
 *   "timestamp": "2026-10-13T10:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Sessao nao encontrada: abc-123",
 *   "path": "/api/sessions/abc-123"
 * }
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata sessao nao encontrada — 404.
     */
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> tratarSessaoNaoEncontrada(
            SessionNotFoundException e, HttpServletRequest requisicao) {

        log.warn("Sessao nao encontrada: {}", e.getSessionId());

        return construirResposta(
                HttpStatus.NOT_FOUND,
                "Not Found",
                e.getMessage(),
                requisicao.getRequestURI()
        );
    }

    /**
     * Trata agente nao encontrado — 404.
     */
    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<ErrorResponse> tratarAgenteNaoEncontrado(
            AgentNotFoundException e, HttpServletRequest requisicao) {

        log.warn("Agente nao encontrado: {}", e.getCodigoAgente());

        return construirResposta(
                HttpStatus.NOT_FOUND,
                "Not Found",
                e.getMessage(),
                requisicao.getRequestURI()
        );
    }

    /**
     * Trata requisicao invalida — 400.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> tratarRequisicaoInvalida(
            InvalidRequestException e, HttpServletRequest requisicao) {

        log.warn("Requisicao invalida: {}", e.getMessage());

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage(),
                requisicao.getRequestURI()
        );
    }

    /**
     * Trata falhas de validacao do Bean Validation (@Valid) — 400.
     *
     * <p>Quando o {@code @Valid} falha em um {@code @RequestBody}, o Spring
     * lanca {@link MethodArgumentNotValidException}. Aqui agregamos as
     * mensagens de todos os campos invalidos.</p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> tratarValidacao(
            MethodArgumentNotValidException e, HttpServletRequest requisicao) {

        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Erro de validacao: {}", mensagem);

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                mensagem,
                requisicao.getRequestURI()
        );
    }

    /**
     * Trata IllegalArgumentException como 400 — mantendo compatibilidade
     * com o handler anterior.
     *
     * <p>Lancada pelo {@code AgentType.porCodigo} quando o codigo
     * informado nao corresponde a nenhum agente.</p>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> tratarArgumentoInvalido(
            IllegalArgumentException e, HttpServletRequest requisicao) {

        log.warn("Argumento invalido: {}", e.getMessage());

        return construirResposta(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage(),
                requisicao.getRequestURI()
        );
    }

    /**
     * Trata falhas do provedor LLM (OpenRouter) — 503.
     *
     * <p>Inclui erros de leitura de resposta, timeout, rate limit, etc.</p>
     */
    @ExceptionHandler(com.openai.errors.OpenAIInvalidDataException.class)
    public ResponseEntity<ErrorResponse> tratarErroOpenAI(
            com.openai.errors.OpenAIInvalidDataException e, HttpServletRequest requisicao) {

        log.warn("Falha do provedor LLM: {}", e.getMessage());

        return construirResposta(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service Unavailable",
                "O provedor do modelo nao respondeu corretamente. Tente novamente.",
                requisicao.getRequestURI()
        );
    }

    /**
     * Trata qualquer excecao nao capturada — 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarErroGenerico(
            Exception e, HttpServletRequest requisicao) {

        log.error("Erro nao tratado em {}: {}", requisicao.getRequestURI(), e.getMessage(), e);

        return construirResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                requisicao.getRequestURI()
        );
    }

    /**
     * Monta a resposta padronizada de erro.
     */
    private ResponseEntity<ErrorResponse> construirResposta(HttpStatus status,
                                                            String error,
                                                            String message,
                                                            String path) {
        ErrorResponse resposta = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                path
        );
        return ResponseEntity.status(status).body(resposta);
    }
}