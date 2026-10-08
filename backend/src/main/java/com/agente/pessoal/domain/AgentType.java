package com.agente.pessoal.domain;

/**
 * Catalogo dos tipos de agentes disponiveis no sistema.
 *
 * <p>Cada valor carrega um codigo estavel (usado por roteadores, endpoints
 * e persistencia) e uma descricao legivel (usada em logs e na interface).</p>
 *
 * <p>O valor {@link #UNCERTAIN} e especial: nao representa um agente real,
 * mas um sinal emitido pelo roteador quando nao consegue classificar
 * com confianca a intencao do usuario.</p>
 */
public enum AgentType {

    /** Agente especialista em planilhas, formulas e analise de dados no Excel. */
    EXCEL("EXCEL", "Especialista em Excel"),

    /** Agente consultor de LGPD, privacidade e protecao de dados. */
    LGPD("LGPD", "Consultor de LGPD"),

    /** Agente para documentacao tecnica, READMEs e guias. */
    DOC("DOC", "Documentacao Tecnica"),

    /** Agente analisador de logs, stack traces e mensagens de erro. */
    LOG("LOG", "Analisador de Logs"),

    /** Agente revisor de seguranca, vulnerabilidades e boas praticas. */
    SEC("SEC", "Revisor de Seguranca"),

    /** Agente orientador de Git, GitHub e fluxos de versionamento. */
    GIT("GIT", "Orientador Git/GitHub"),

    /**
     * Valor especial usado pelo roteador quando nao tem certeza da intencao.
     * Nao representa um agente real.
     */
    UNCERTAIN("UNCERTAIN", "Intencao nao identificada");

    private final String codigo;
    private final String descricao;

    AgentType(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se este tipo representa um agente real.
     *
     * @return {@code true} se for um agente real
     */
    public boolean isAgenteReal() {
        return this != UNCERTAIN;
    }

    /**
     * Busca um {@link AgentType} pelo seu codigo, ignorando maiusculas/minusculas.
     *
     * @param codigo codigo a ser buscado (ex: "EXCEL")
     * @return o {@link AgentType} correspondente
     * @throws IllegalArgumentException se o codigo nao corresponder a nenhum agente
     */
    public static AgentType porCodigo(String codigo) {
        for (AgentType tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Codigo de agente desconhecido: " + codigo);
    }
}