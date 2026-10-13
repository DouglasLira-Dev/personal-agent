package com.agente.pessoal.persistence.repository;

import com.agente.pessoal.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de mensagens.
 *
 * <p>Estende {@link JpaRepository}, herdando operacoes CRUD prontas.
 * Adiciona consultas customizadas para recuperar o historico de uma
 * conversa e limpar mensagens ao deletar a conversa.</p>
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * Recupera todas as mensagens de uma conversa, em ordem cronologica.
     *
     * <p>O nome do metodo segue a convencao do Spring Data JPA:
     * {@code findBy} + {@code ConversationId} + {@code OrderBy} + {@code TimestampAsc}.</p>
     *
     * @param conversationId UUID da conversa
     * @return lista de mensagens ordenadas por timestamp ascendente
     */
    List<MessageEntity> findByConversationIdOrderByTimestampAsc(String conversationId);

    /**
     * Remove todas as mensagens de uma conversa.
     *
     * <p>Necessario porque {@code ConversationEntity} usa {@code orphanRemoval=true}
     * com {@code CascadeType.ALL}, mas em alguns casos (bulk delete) o
     * cascade pode nao ser acionado. Esta consulta garante a limpeza.</p>
     *
     * <p><b>Atencao:</b> o {@code @Modifying} e obrigatorio para queries
     * de DELETE/UPDATE. O {@code clearAutomatically} limpa o cache do
     * persistence context apos a execucao.</p>
     *
     * @param conversationId UUID da conversa
     * @return numero de mensagens removidas
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MessageEntity m WHERE m.conversation.id = :conversationId")
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * Conta quantas mensagens existem em uma conversa.
     *
     * @param conversationId UUID da conversa
     * @return numero de mensagens
     */
    long countByConversationId(String conversationId);
}