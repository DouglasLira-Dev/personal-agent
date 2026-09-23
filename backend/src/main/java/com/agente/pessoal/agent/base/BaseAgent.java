package com.agente.pessoal.agent.base;

import com.agente.pessoal.domain.AgentType;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

/**
 * Classe base para todos os agentes do sistema.
 * 
 * <p>Concentra o comportamento comum: construcao do {@link ChatClient} com
 * System Prompt e Advisor de memoria, alem da mecanica de envio de mensagens.</p>
 * 
 * <p>Subclasses devem fornecer o System Prompt (no construtor) e implementar
 * os metodos {@link #getTipo()} e {@link #getDescricao()}.</p>
 * 
 * <p><b>Importante:</b> a partir do Spring AI 1.0.7 / 1.1.6, o
 * conversationId da memoria precisa ser passado explicitamente. Caso
 * contrario, a aplicacao lanca excecao (medida de seguranca contra 
 * vazamento de dados entre usuarios).</p>
*/

public abstract class BaseAgent {

    /** Cliente de chat configurado com System Prompt e memoria */
    protected final ChatClient chatClient;

    /** Memoria de conversa injetada, usada para configurar o advisor.*/
    protected final ChatMemory chatMemory;
    
    /**
     * Construtor protegido. Recebe as dependencias injetadas pelo Spring e o
     * System Prompt definido pela subclasse.
     * 
     * @param builder       construtor de ChatClient, injetado pelo Spring
     * @param chatMemory    memoria de conversa, injetada pelo Spring
     * @param systemPrompt  System Prompt do agente, definido pela subclasse
    */
   protected BaseAgent(ChatClient.Builder builder,
                        ChatMemory chatMemory,
                        String systemPrompt) {
        this.chatMemory = chatMemory;
        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * Envia uma mensagem para o agente e retorna a resposta.
     * 
     * <p>O {@code sessionId} identifica a conversa na memoria. Mensagens
     * com o mesmo sessionId compartilham contexto; sessionIds diferentes
     * sao conversas independentes.</p>
     * 
     * @param mensagem  texto enviado pelo usuario
     * @param sessionId identificador da sessao da conversa
     * @return resposta gerada pelo modelo
    */
   public String chat(String mensagem, String sessionId) {
    return chatClient.prompt()
            .user(mensagem)
            .advisors(advisor -> advisor.param(
                    ChatMemory.CONVERSATION_ID, sessionId))
            .call()
            .content();
   }

   /**
    * Retorna o tipo deste agente.
    * 
    * @return valor de {@link AgentType} correspondente
   */
  public abstract AgentType getTipo();

  /**
   * Retorna a descricao legivel deste agente.
   * 
   * @return descricao do agente.
  */
   public abstract String getDescricao();
}