package com.agente.pessoal.application;

import com.agente.pessoal.domain.AgentType;
import com.agente.pessoal.persistence.entity.ConversationEntity;
import com.agente.pessoal.persistence.entity.MessageEntity;
import com.agente.pessoal.persistence.repository.ConversationRepository;
import com.agente.pessoal.persistence.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servico de gerenciamento de sessoes (conversas) e mensagens.
 *
 * <p>Responsavel por criar, listar, buscar e deletar conversas, alem
 * de persistir mensagens individuais. Atualiza automaticamente o
 * {@code updatedAt} da conversa a cada nova mensagem.</p>
 *
 * <p><b>Nota:</b> esta classe gerencia o <i>historico persistido</i>.
 * A memoria que o agente usa para contexto continua sendo gerenciada
 * pelo Spring AI (tabela SPRING_AI_CHAT_MEMORY).</p>
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    /** Tamanho maximo do titulo gerado a partir da primeira mensagem. */
    private static final int TAMANHO_MAX_TITULO = 80;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public SessionService(ConversationRepository conversationRepository,
                          MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    // ============================================
    // Criacao de sessoes
    // ============================================

    /**
     * Cria uma nova conversa com UUID gerado.
     *
     * @return a conversa criada
     */
    @Transactional
    public ConversationEntity criarSessao() {
        return criarSessao(null);
    }

    /**
     * Cria uma nova conversa com titulo customizado.
     *
     * @param titulo titulo inicial (pode ser nulo)
     * @return a conversa criada
     */
    @Transactional
    public ConversationEntity criarSessao(String titulo) {
        String id = UUID.randomUUID().toString();
        ConversationEntity conversa = new ConversationEntity(id);
        conversa.setTitle(titulo);

        ConversationEntity salva = conversationRepository.save(conversa);
        log.info("Sessao criada: id={}, titulo={}", salva.getId(), salva.getTitle());

        return salva;
    }

    // ============================================
    // Listagem e busca
    // ============================================

    /**
     * Lista todas as conversas, mais recentes primeiro.
     *
     * @return lista ordenada por updatedAt decrescente
     */
    @Transactional(readOnly = true)
    public List<ConversationEntity> listarSessoes() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc();
    }

    /**
     * Busca uma conversa pelo ID.
     *
     * @param id UUID da conversa
     * @return {@link Optional} com a conversa
     */
    @Transactional(readOnly = true)
    public Optional<ConversationEntity> buscarSessao(String id) {
        return conversationRepository.findById(id);
    }

    /**
     * Retorna as mensagens de uma conversa em ordem cronologica.
     *
     * @param conversationId UUID da conversa
     * @return lista de mensagens ordenadas por timestamp ascendente
     */
    @Transactional(readOnly = true)
    public List<MessageEntity> listarMensagens(String conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    // ============================================
    // Remocao
    // ============================================

    /**
     * Remove uma conversa e todas as suas mensagens.
     *
     * <p>Chama explicitamente o bulk delete das mensagens antes de
     * remover a conversa, garantindo limpeza mesmo sem cascade.</p>
     *
     * @param id UUID da conversa
     * @return {@code true} se a conversa existia e foi removida
     */
    @Transactional
    public boolean deletarSessao(String id) {
        if (!conversationRepository.existsById(id)) {
            log.warn("Tentativa de deletar sessao inexistente: {}", id);
            return false;
        }

        int mensagensRemovidas = messageRepository.deleteByConversationId(id);
        conversationRepository.deleteById(id);

        log.info("Sessao removida: id={}, mensagens removidas={}", id, mensagensRemovidas);
        return true;
    }

    // ============================================
    // Persistencia de mensagens
    // ============================================

    /**
     * Persiste uma mensagem em uma conversa.
     *
     * <p>Se a conversa nao existir, cria automaticamente com o ID fornecido
     * (compatibilidade com sessoes antigas ou criadas fora deste servico).</p>
     *
     * @param conversationId UUID da conversa
     * @param role           papel do autor (USER ou ASSISTANT)
     * @param content        conteudo textual
     * @param agent          tipo do agente que respondeu (nulo se for USER)
     * @return a mensagem persistida
     */
    @Transactional
    public MessageEntity salvarMensagem(String conversationId,
                                        String role,
                                        String content,
                                        AgentType agent) {

        ConversationEntity conversa = conversationRepository.findById(conversationId)
                .orElseGet(() -> {
                    log.warn("Conversa {} nao encontrada — criando automaticamente", conversationId);
                    ConversationEntity nova = new ConversationEntity(conversationId);
                    return conversationRepository.save(nova);
                });

        String agentType = (agent != null) ? agent.getCodigo() : null;
        MessageEntity mensagem = new MessageEntity(role, content, agentType);
        conversa.adicionarMensagem(mensagem);

        MessageEntity salva = messageRepository.save(mensagem);

        log.debug("Mensagem persistida: conversa={}, role={}, agent={}, tamanho={}",
                conversationId, role, agentType, content.length());

        return salva;
    }

    /**
     * Atualiza o titulo da conversa com base na primeira mensagem, se ainda nao tiver titulo.
     *
     * <p>O titulo e truncado em {@value #TAMANHO_MAX_TITULO} caracteres.</p>
     *
     * @param id           UUID da conversa
     * @param primeiraMensagem conteudo da primeira mensagem do usuario
     */
    @Transactional
    public void atualizarTituloSeNecessario(String id, String primeiraMensagem) {
        conversationRepository.findById(id).ifPresent(conversa -> {
            if (conversa.getTitle() != null && !conversa.getTitle().isBlank()) {
                return;
            }

            String titulo = gerarTitulo(primeiraMensagem);
            conversa.setTitle(titulo);
            conversationRepository.save(conversa);

            log.info("Titulo da conversa {} definido: {}", id, titulo);
        });
    }

    // ============================================
    // Metodos privados
    // ============================================

    /**
     * Gera um titulo curto a partir da primeira mensagem do usuario.
     *
     * @param mensagem conteudo da mensagem
     * @return titulo truncado
     */
    private String gerarTitulo(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return "Conversa sem titulo";
        }

        String limpo = mensagem.trim().replaceAll("\\s+", " ");

        if (limpo.length() <= TAMANHO_MAX_TITULO) {
            return limpo;
        }

        return limpo.substring(0, TAMANHO_MAX_TITULO - 3) + "...";
    }
}