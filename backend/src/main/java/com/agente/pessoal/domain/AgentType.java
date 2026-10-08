package com.agente.pessoal.domain;

/**
 * Catalogo dos tipos de agentes disponiveis no sistema.
 *
 * <p>Cada valor carrega um codigo estavel (usado por roteadores, endpoints e persistencia) e descritivo (usada em logs e na interface).</p>
 *
 * <p>O valor {@link #UNCERTAIN}e especial: nao representa um agente real,
 * mas um sinal emitido pelo roteador quando nao consegue classificar
 * com confianca a intencao do usuario.</p>
 */

public enum AgentType {
    /**
     * Agente especialista em planilhas, formulas e analise de dados no Excel.
     */
    EXCEL("EXCEL", "Especialista em Excel"),

    /** Agente consultor de LGPD, privacidade e protecao de dados.*/
    LGPD("LGPD", "Consultor de LGPD"),

    /** Agente para documentacao tecnica, READMEs e guias.*/
    DOC("DOC", "Documentacao Tecnica"),

    /** Agente analisador de Logs, stack traces e mensagens de erro.*/
    LOG("LOG", "Analisador de Logs"),

    /** Agente revisor de seguranca, vulnerabilidades e boas praticas.*/
    SEC("SEC", "Revisor de Seguranca"),

    /** Agente orientador de Git, GitHub e fluxos de versionamento.*/
    GIT("GIT", "Orientador de Git/GitHub"),

    /**
     * Valor especial usado pelo roteador quando nao tem certeza da intencao.
     * Nao representa um agente real.
     */
    UNCERTAIN("UNCERTAIN", "Intencao nao identificada");

    /** Codigo estavel do agente, usado por roteadores e integracoes. */
    private final String codigo;

    /** Descricao legivel, exibida em logs e na interface do usuario. */
    private final String descricao;

    /**
     * Construtor interno do enum.
     *
     * @param codigo    codigo estavel do agente
     * @param descricao descricao legivel do agente
     */
    AgentType(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * Retorna o codigo estavel do agente.
     *
     * @return codigo do agente (ex: "EXCEL")
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Retorna a descricao legivel do agente
     *
     * @return descricao do agente (ex: "Especialista em Excel")
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se este tipo representa um agente real.
     *
     *<p>{@link #UNCERTAIN} nao e um agente real, e apenas um sinal
     *do roteador.</p>
     *
     *@return {@code true} se for um agente real
     */

    /**
     * Busca um {@link AgentType} pelo seu codigo, ignorando maiusculas/minusculas.
     *
     * <p>Util para converter valores vindos de requisicoes HTTP, mensagens ou configuracoes externas em um valor tipado do enum.</p>
     *
     * @param codigo codigo a ser buscado (ex: "EXCEL")
     * @return o {@link AgentType} correspondente
     * @throws IllegalArgumentException se o codigo nao corresponder a nenhum agente
     */
    public static AgentType porCodigo(String codigo) {
        for (AgentType tipo: values()){
            if (tipo.codigo.equalsIgnoreCase(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Codigo de agente desconhecido: " + codigo);
    }
}