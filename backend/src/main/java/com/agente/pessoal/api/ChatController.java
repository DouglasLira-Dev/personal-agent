package com.agente.pessoal.api;

import com.agente.pessoal.api.dto.ChatRequest;
import com.agente.pessoal.api.dto.ChatResponse;
import com.agente.pessoal.application.ChatService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Controller REST do chat
 * 
 * <p>Expoe o endpoint POST /api/chat, que recebe uma mensagem do usuario
 * e retorna a resposta do agente responsavel.</p>
 */
@RestController
@RequestMapping("api/chat")
public class ChatController {

    /** Servico que orquestra a chamada ao agente */
    private final ChatService chatService;

    /**
     * Construtor com injecao do ChatService.
     *
     *@param chatService servico de orquestracao do chat
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Recebe uma mensagem do usuario e retorna a resposta do agente.
     *
     * <p>O {@code @Valid} aciona a validacao do {@link ChatRequest},
     * garantindo que a mensagem nao venha em branco.</p>
     *
     * @param requisicao dados da mensagem enviada pelo usuario
     * @return resposta do agente com status 200 (OK)
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest requisicao) {
        ChatResponse resposta = chatService.chat(requisicao);
        return ResponseEntity.ok(resposta);
    }
}
