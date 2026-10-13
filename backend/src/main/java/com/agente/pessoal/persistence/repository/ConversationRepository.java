package com.agente.pessoal.persistence.repository;

import com.agente.pessoal.persistence.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de conversas.
 *
 * <p>Estende {@link JpaRepository}, herdando operacoes CRUD prontas
 * (save, findById, deleteById, etc.). Adiciona consultas customizadas
 * para listagem e busca.</p>
 */
@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {

    /**
     * Lista todas as conversas ordenadas pela ultima atualizacao (mais recente primeiro).
     *
     * <p>Util para o front montar a lista de conversas recentes.</p>
     *
     * @return lista de conversas ordenadas por updatedAt decrescente
     */
    List<ConversationEntity> findAllByOrderByUpdatedAtDesc();

    /**
     * Busca uma conversa pelo ID.
     *
     * <p>Encapsula o {@code findById} herdado para retornar {@link Optional}
     * de forma explicita no contrato do repositorio.</p>
     *
     * @param id UUID da conversa
     * @return {@link Optional} com a conversa, ou vazio se nao existir
     */
    @Override
    Optional<ConversationEntity> findById(String id);
}