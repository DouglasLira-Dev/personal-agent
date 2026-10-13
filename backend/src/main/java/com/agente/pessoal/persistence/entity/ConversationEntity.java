package com.agente.pessoal.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma conversa (sessao) do usuario.
 *
 * <p>Uma conversa tem um ID (UUID), um titulo opcional, timestamps
 * de criacao/atualizacao e uma lista de mensagens.</p>
 *
 * <p><b>Importante:</b> esta entidade e o modelo de historico do front.
 * A memoria que o agente usa para contexto e gerenciada separadamente
 * pelo Spring AI (tabela SPRING_AI_CHAT_MEMORY).</p>
 */
@Entity
@Table(name = "conversations")
public class ConversationEntity {

    /** ID da conversa (UUID). */
    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    /** Titulo da conversa (geralmente a primeira mensagem resumida). */
    @Column(name = "title", length = 255)
    private String title;

    /** Data de criacao. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data da ultima interacao. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Mensagens da conversa (relacionamento 1:N). */
    @OneToMany(
            mappedBy = "conversation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<MessageEntity> messages = new ArrayList<>();

    /** Construtor padrao exigido pelo JPA. */
    public ConversationEntity() {
    }

    /**
     * Construtor com o ID obrigatorio.
     *
     * @param id UUID da conversa
     */
    public ConversationEntity(String id) {
        this.id = id;
    }

    /**
     * Preenche os timestamps antes de persistir.
     */
    @PrePersist
    public void antesDePersistir() {
        LocalDateTime agora = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = agora;
        }
        updatedAt = agora;
    }

    /**
     * Atualiza o timestamp antes de atualizar.
     */
    @PreUpdate
    public void antesDeAtualizar() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Adiciona uma mensagem a conversa, mantendo os dois lados do
     * relacionamento consistentes.
     *
     * @param mensagem mensagem a adicionar
     */
    public void adicionarMensagem(MessageEntity mensagem) {
        messages.add(mensagem);
        mensagem.setConversation(this);
    }

    // ============================================
    // Getters e Setters
    // ============================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
    }
}