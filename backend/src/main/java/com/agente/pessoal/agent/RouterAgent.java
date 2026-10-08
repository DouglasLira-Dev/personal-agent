package com.agente.pessoal.agent;

import com.agente.pessoal.domain.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Roteador de agentes.
 *
 * <p>Classifica a mensagem do usuario em um dos modulos disponiveis.
 * Diferente dos agentes normais, este componente:
 * <ul>
 *   <li>Nao responde ao usuario — apenas classifica</li>
 *   <li>Nao usa memoria de conversa — classificacao e stateless</li>
 *   <li>Retorna um {@link AgentType}, nao texto livre</li>
 * </ul>
 *
 * <p>Nao estende {@link com.agente.pessoal.agent.base.BaseAgent} de proposito:
 * a classificacao nao precisa de memoria nem de sessionId.</p>
 */
@Component
public class RouterAgent {

    private static final Logger log = LoggerFactory.getLogger(RouterAgent.class);

    /** Cliente de chat sem memoria, usado apenas para classificacao. */
    private final ChatClient chatClient;

    /**
     * Construtor que recebe o builder auto-configurado pelo Spring AI.
     *
     * <p>Importante: NAO configuramos advisors de memoria. Cada chamada
     * de classificacao e independente.</p>
     *
     * @param builder builder de ChatClient injetado pelo Spring
     */
    public RouterAgent(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Classifica a mensagem do usuario em um {@link AgentType}.
     *
     * @param mensagem mensagem do usuario
     * @return o tipo do agente mais adequado, ou {@link AgentType#UNCERTAIN}
     *         se nao for possivel classificar
     */
    public AgentType classificar(String mensagem) {
        String prompt = montarPrompt(mensagem);

        String bruto = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return normalizar(bruto);
    }

    /**
     * Monta o prompt de classificacao a partir da mensagem do usuario.
     *
     * @param mensagem mensagem original do usuario
     * @return prompt completo para o LLM
     */
    private String montarPrompt(String mensagem) {
        return """
                Voce e um roteador de agentes. Sua unica funcao e classificar a mensagem
                do usuario em um dos modulos disponiveis.

                Modulos disponiveis:
                - EXCEL: formulas, VBA, macros, planilhas, tabelas dinamicas
                - LGPD: dados pessoais, privacidade, conformidade, boas praticas administrativas
                - DOC: README, LICENSE, documentacao de API, Javadoc, CHANGELOG
                - LOG: erros, stack traces, logs de servidor, analise de excecoes
                - SEC: seguranca de codigo, vulnerabilidades, OWASP, boas praticas seguras
                - GIT: branches, commits, fluxo de trabalho, pull requests, conflitos

                Regras:
                1. Responda APENAS com o codigo do modulo (ex: EXCEL).
                2. Nao adicione explicacoes, pontuacao ou texto extra.
                3. Se a mensagem cruzar multiplos modulos, escolha o PRINCIPAL.
                4. Se nao tiver certeza, responda: UNCERTAIN

                Mensagem do usuario: "%s"
                """.formatted(mensagem);
    }

    /**
     * Normaliza a resposta bruta do LLM e converte para {@link AgentType}.
     *
     * <p>Aplica trim, uppercase e remove caracteres nao alfabeticos
     * (como pontuacao que o modelo possa ter adicionado). Se o valor
     * resultante nao corresponder a nenhum tipo, retorna
     * {@link AgentType#UNCERTAIN} em vez de lancar excecao.</p>
     *
     * @param bruto resposta crua do LLM
     * @return o AgentType correspondente ou UNCERTAIN
     */
    private AgentType normalizar(String bruto) {
        if (bruto == null || bruto.isBlank()) {
            log.warn("RouterAgent recebeu resposta vazia do LLM");
            return AgentType.UNCERTAIN;
        }

        String limpo = bruto.trim().toUpperCase().replaceAll("[^A-Z]", "");

        try {
            return AgentType.porCodigo(limpo);
        } catch (IllegalArgumentException e) {
            log.warn("RouterAgent retornou valor invalido: '{}' (normalizado: '{}')",
                    bruto, limpo);
            return AgentType.UNCERTAIN;
        }
    }
}