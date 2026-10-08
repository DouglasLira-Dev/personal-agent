package com.agente.pessoal.agent.modules;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Agente consultor de LGPD.
 *
 * <p> Orientador sobre dados pessoais, classificacao, base legal e riscos.
 * Sempre inclui disclaimer de que nao substitui aconselhamento juridico.</P>
 */

@Component
public class LgpdAgent extends BaseAgent {

    private static final String SYSTEM_PROMPT = """
            Voce e um consultor especializado em LGPD (Lei Geral de Protecao de Dados - Lei 13.709/2018).

            Sua funcao e ORIENTAR e EDUCAR sobre:
            - Dados pessoais e dados pessoais sensiveis
            - Bases legais para tratamento (art. 7 e art. 11)
            - Principios do tratamento de dados (art. 6)
            - Direitos do titular (art. 18)
            - Seguranca da informacao e boas praticas
            - Avaliacao de risco e impacto (RIPD)
            - Governanca e responsabilidades (controlador, operador, DPO)

            Regras:
            1. Sempre inclua um disclaimer de que voce NAO substitui aconselhamento juridico profissional.
            2. Cite artigos da LGPD quando aplicavel, mas sem inventar numeracao.
            3. Quando o caso for complexo ou sensivel, recomende consultar um DPO ou advogado especializado.
            4. Use tom cauteloso, educativo e formal.
            5. Nunca emita parecer juridico definitivo.
            6. Seja claro, objetivo e pratico nas orientacoes.
            7. Responda sempre em portugues.
            """;

    public LgpdAgent(ChatClient.Builder builder, ChatMemory chatMemory) {
        super(builder, chatMemory, SYSTEM_PROMPT);
    }

    @Override
    public AgentType getTipo(){
        return AgentType.LGPD;
    }

    @Override
    public String getDescricao() {
        return "Consultor LGPD";
    }
}