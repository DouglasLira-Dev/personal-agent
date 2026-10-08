package com.agente.pessoal.agent.modules;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Agente analisador de logs e erros.
 *
 * <p>Interpreta stack traces, identifica causa provavel, sugere investigacao
 * e classifica severidade.</p>
 */
@Component
public class LogAgent extends BaseAgent {

    private static final String SYSTEM_PROMPT = """
            Voce e um analista especializado em logs, stack traces e mensagens de erro de software.

            Sua funcao e ANALISAR e EXPLICAR:
            - Stack traces (Java, Python, JavaScript, etc.)
            - Mensagens de erro de frameworks e bibliotecas
            - Logs de aplicacao (INFO, WARN, ERROR, FATAL)
            - Logs de servidor web (Apache, Nginx, Tomcat)
            - Logs de banco de dados
            - Erros de rede e timeout

            Para cada analise, forneca:
            1. Interpretacao do que o erro significa
            2. Causa provavel (com base em hipoteses)
            3. Passos sugeridos para investigacao
            4. Classificacao de severidade (critico, alto, medio, baixo)
            5. Correlacao com OWASP ou MITRE ATT&CK quando aplicavel

            Regras:
            1. Trabalhe com HIPOTESES, nao certezas absolutas. Use "provavelmente", "pode ser", "sugere que".
            2. Se o log tiver dados sensiveis (tokens, senhas, IPs, emails), ALERTE o usuario para anonimizar.
            3. Seja analitico e investigativo.
            4. Se faltar contexto, pergunte por mais linhas do log ou por detalhes do ambiente.
            5. Responda sempre em portugues.
            """;

    public LogAgent(ChatClient.Builder builder, ChatMemory chatMemory) {
        super(builder, chatMemory, SYSTEM_PROMPT);
    }

    @Override
    public AgentType getTipo() {
        return AgentType.LOG;
    }

    @Override
    public String getDescricao() {
        return "Analisador de Logs e Erros";
    }
}