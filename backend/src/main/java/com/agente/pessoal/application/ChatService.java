package com.agente.pessoal.application;

import com.agente.pessoal.agent.modules.ExcelAgent;
import com.agente.pessoal.api.dto.ChatRequest;
import com.agente.pessoal.api.dto.ChatResponse;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servico de orquestracao do chat.
 *
 * <p>Nesta fase, delega diretamente ao {@link ExcelAgent}, sem roteador.
 * O roteador sera adicionado na Fase 3, quando houver multiplos agentes.</p>
 */
@Service
public class ChatService {
    /** Agente Excel, injetado pelo Spring.  */
    private final ExcelAgent excelAgent;

    /**
     * Construtor com injecao do agente Excel.
     *
     * @param excelAgent agente especialista em Excel
     */
    public ChatService(ExcelAgent excelAgent) {
        this.excelAgent = excelAgent;
    }

    /**
     * Processa uma requisicao de chat e retorna a resposta do agente.
     *
     * <p>Se o {@code sessionId} vier nulo ou vazio, gera um UUID
     * automaticamente para garantir que a memoria de conversa funcione.</p>
     *
     * @param requisicao dados da mensagem enviada pelo usuario
     * @return resposta do agente com metadados
     */
    public ChatResponse chat(ChatRequest requisicao) {
        String sessionId = normalizarSessionId(requisicao.sessionId());

        String resposta = excelAgent.chat(requisicao.message(), sessionId);

        return new ChatResponse(
                sessionId,
                excelAgent.getTipo(),
                resposta,
                LocalDateTime.now()
        );
    }

    /**
     * Garante que o sessionId nunca seja nulo ou vazio.
     *
     * <p>Se estiver em branco, gera um UUID aleatorio. isso evita que a
     * memoria de conversa quebre por causa de um identificador invalido.</p>
     *
     * @param sessionId identificador recebido pela requisicao
     * @return sessionId valido (o original ou um UUID novo)
     */
    private String normalizarSessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return sessionId;
    }
}