package com.agente.pessoal.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma mensagem dentro de uma conversa.
 *
 * <p>Cada mensagem tem um autor (USER ou ASSISTANT), conteudo textual,
 * timestamp e, quando for resposta de um agente, o tipo do agente que
 * respondeu.</p>
 */
@Entity
@Table(name = "messages")
public class MessageEntity {

    /** ID auto-incrementado da mensagem. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Conversa a que esta mensagem pertence. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    /** Papel do autor: USER ou ASSISTANT. */
    @Column(name = "role", length = 20, nullable = false)
    private String role;

    /** Conteudo textual da mensagem. */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * Tipo do agente que respondeu.
     * Nulo quando a mensagem e do usuario.
     */
    @Column(name = "agent_type", length = 20)
    private String agentType;

    /** Momento em que a mensagem foi enviada. */
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /** Construtor padrao exigido pelo JPA. */
    public MessageEntity() {
    }

    /**
     * Construtor completo.
     *
     * @param role      papel do autor (USER ou ASSISTANT)
     * @param content   conteudo textual
     * @param agentType tipo do agente (pode ser nulo)
     */
    public MessageEntity(String role, String content, String agentType) {
        this.role = role;
        this.content = content;
        this.agentType = agentType;
    }

    /**
     * Preenche o timestamp antes de persistir.
     */
    @PrePersist
    public void antesDePersistir() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // ============================================
    // Getters e Setters
    // ============================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConversationEntity getConversation() {
        return conversation;
    }

    public void setConversation(ConversationEntity conversation) {
        this.conversation = conversation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}