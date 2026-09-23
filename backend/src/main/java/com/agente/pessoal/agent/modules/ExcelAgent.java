package com.agente.pessoal.agent.modules;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Agente especialista em Microsoft Excel.
 * 
 * <p>Orienta e ensina o usuario sobre formulas, tabelas dinamicas,
 * Power Query, VBA e boas praticas de estruturacao de planilhas.</p>
 * 
 * <p>Nao gera arquivos .xlsx: apenas fornece o conhecimento necessario
 * para o usuario aplicar.</p>
 */
@Component
public class ExcelAgent extends BaseAgent {
    /**
     * System Prompt do agente Excel
     * 
     * <p>Define a identidade, especialidades, regras de resposta e 
     * limitacoes do agente.</p>
     */
    private static final String SYSTEM_PROMPT = """
            Voce e um especialista senior em Microsoft Excel, com dominio avancado de:

            - Formulas: PROCV, SOMASES, INDICE/CORRESP, CONT.SES, SE aninhado, etc.
            - Tabelas Dinamicas e Power Query
            - VBA e Macros para automacao
            - Boas praticas de estruturacao de planilhas

            Sua funcao e ORIENTAR e ENSINAR o usuario.

            Regras:
            1. Voce nao gera arquivos .xlsx. Voce fornece o conhecimento.
            2. Sempre que sugerir uma formula, explique o que cada parte faz.
            3. Ao fornecer codigo VBA, comente as linhas principais.
            4. Se a pergunta for ambigua, pergunte antes de responder.
            5. Se a funcao nao existir na versao do Excel do usuario, avise.
            6. Seja direto, pratico e objetico.
    """;

    /**
     * Construtor do agente Excel
     * 
     * <p>Repassa as dependencias para {@link BaseAgent}, junto com o 
     * System Prompt especifico deste agente.</p>
     * 
     * @param builder       construtor de ChatClient, injetado pelo spring
     * @param chatMemory    memoria de conversa, injetada pelo Spring
     */
    public ExcelAgent(ChatClient.Builder builder, ChatMemory chatMemory) {
        super(builder, chatMemory, SYSTEM_PROMPT);
    }

    /**
     * Retorna o tipo deste agente.
     * 
     * @return {@link AgentType#EXCEL}
     */
    public AgentType getTipo() {
        return AgentType.EXCEL;
    }

    /**
     * Retorna a descricao legivel deste agente.
     * 
     * @return descricao do agente
     */
    @Override
    public String getDescricao() {
        return "ESPECIALISTA em Excel";
    }
}