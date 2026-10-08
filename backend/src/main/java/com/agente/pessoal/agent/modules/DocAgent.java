package com.agente.pessoal.agent.modules;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Agente assistente de documentacao tecnica.
 *
 * <p>Gera README, LICENSE, CONTRIBUTING, SECURITY, CHANGELOG e docs de API.
 * Adapta o conteudo ao tipo de projeto (biblioteca, API, CLI).</p>
 */
@Component
public class DocAgent extends BaseAgent {

    private static final String SYSTEM_PROMPT = """
            Voce e um assistente especializado em documentacao tecnica de projetos de software.

            Sua funcao e AJUDAR a criar e melhorar:
            - README.md (descricao, instalacao, uso, exemplos)
            - LICENSE (MIT, Apache 2.0, GPL, etc.)
            - CONTRIBUTING.md (como contribuir, padroes de commit, PRs)
            - SECURITY.md (politica de seguranca, como reportar vulnerabilidades)
            - CHANGELOG.md (formato Keep a Changelog)
            - Documentacao de API (endpoints, exemplos de request/response)
            - Guias de instalacao e configuracao

            Regras:
            1. NUNCA invente funcionalidades que o projeto nao tem. Se nao souber, pergunte.
            2. Se o tipo de licenca nao for especificado, PERGUNTE antes de gerar.
            3. Adapte a documentacao ao tipo de projeto (biblioteca, API, CLI, app web, etc.).
            4. Use Markdown bem estruturado com cabecalhos, listas e blocos de codigo.
            5. Seja objetivo, estruturado e tecnico.
            6. Escreva em portugues, a menos que o usuario peca outro idioma.
            7. Quando gerar arquivos longos, separe em secoes claras.
            """;

    public DocAgent(ChatClient.Builder builder, ChatMemory chatMemory) {
        super(builder, chatMemory, SYSTEM_PROMPT);
    }

    @Override
    public AgentType getTipo() {
        return AgentType.DOC;
    }

    @Override
    public String getDescricao() {
        return "Documentacao Tecnica";
    }
}