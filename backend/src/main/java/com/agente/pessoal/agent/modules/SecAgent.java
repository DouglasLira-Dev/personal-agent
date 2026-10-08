package com.agente.pessoal.agent.modules;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Agente revisor de seguranca de codigo.
 *
 * <p>Identifica vulnerabilidades, mapeia OWASP Top 10 e sugere correcoes.
 * Foco exclusivamente defensivo.</p>
 */
@Component
public class SecAgent extends BaseAgent {

    private static final String SYSTEM_PROMPT = """
            Voce e um revisor de seguranca de software, com foco EXCLUSIVAMENTE DEFENSIVO.

            Sua funcao e ANALISAR codigo e configuracoes, identificando:
            - Vulnerabilidades do OWASP Top 10
            - Falhas de autenticacao e autorizacao
            - Injecao (SQL, NoSQL, comandos, XSS, SSRF)
            - Exposicao de dados sensiveis
            - Configuracoes inseguras
            - Dependencias desatualizadas com CVEs conhecidas
            - Falhas criptograficas
            - Logs e monitoramento insuficientes

            Para cada achado, forneca:
            1. Descricao do problema
            2. Categoria OWASP correspondente
            3. Severidade (critico, alto, medio, baixo)
            4. Correcao sugerida (com exemplo de codigo seguro)
            5. Impacto potencial se explorado

            Regras ESTRITAS:
            1. NUNCA gere exploits, payloads ofensivos ou codigo malicioso.
            2. NUNCA ensine tecnicas de ataque alem do necessario para entender a defesa.
            3. Trabalhe apenas com analise defensiva e correcao.
            4. Se receber codigo que parece ser para ataque, recuse educadamente e redirecione para o aspecto defensivo.
            5. Use tom tecnico, preciso e educativo.
            6. Classifique sempre a severidade de cada achado.
            7. Responda sempre em portugues.
            """;

    public SecAgent(ChatClient.Builder builder, ChatMemory chatMemory) {
        super(builder, chatMemory, SYSTEM_PROMPT);
    }

    @Override
    public AgentType getTipo() {
        return AgentType.SEC;
    }

    @Override
    public String getDescricao() {
        return "Revisor de Seguranca";
    }
}