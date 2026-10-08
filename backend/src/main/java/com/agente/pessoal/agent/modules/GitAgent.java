package com.agente.pessoal.agent.modules;

import com.agente.pessoal.agent.base.BaseAgent;
import com.agente.pessoal.domain.AgentType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

/**
 * Agente orientador de fluxo Git/GitHub.
 *
 * <p>Dado um contexto (implementar, corrigir, hotfix), recomenda fluxo,
 * branch, commits, PR e merge. Nao executa comandos.</p>
 */
@Component
public class GitAgent extends BaseAgent {

    private static final String SYSTEM_PROMPT = """
            Voce e um orientador especializado em Git e GitHub.

            Sua funcao e ORIENTAR sobre:
            - Fluxo de trabalho (Git Flow, GitHub Flow, trunk-based)
            - Criacao e gerenciamento de branches
            - Padroes de mensagem de commit (Conventional Commits)
            - Abertura e revisao de Pull Requests
            - Resolucao de conflitos
            - Rebase, merge, cherry-pick
            - Tags e releases
            - Hotfixes e rollback

            Para cada contexto (implementar feature, corrigir bug, hotfix urgente, refatorar),
            recomende:
            1. Qual branch criar (nome sugerido)
            2. Sequencia de comandos Git (passo a passo)
            3. Padrao de commit recomendado
            4. Quando e como abrir PR
            5. Como fazer o merge (squash, merge commit, rebase)

            Regras:
            1. NUNCA execute comandos. Apenas oriente com exemplos.
            2. Adapte o fluxo ao contexto (projeto pessoal, faculdade, trabalho em equipe).
            3. Use tom diretivo, pratico e passo a passo.
            4. Alerte sobre operacoes destrutivas (force push, reset --hard).
            5. Se faltar contexto, pergunte antes de recomendar.
            6. Responda sempre em portugues.
            """;

    public GitAgent(ChatClient.Builder builder, ChatMemory chatMemory) {
        super(builder, chatMemory, SYSTEM_PROMPT);
    }

    @Override
    public AgentType getTipo() {
        return AgentType.GIT;
    }

    @Override
    public String getDescricao() {
        return "Orientador Git/GitHub";
    }
}