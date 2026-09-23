package com.agente.pessoal.domain;

/**
 * Catalogo dos tipos de agentes disponiveis no sistema.
 * 
 * <p>Cada valor carrega um codigo estavel (usado por roteadores, endpoints e persistencia) e descritivo (usada em logs e na interface).</p>
 * 
 * <p>Nesta fase existe apenas o agente Excel. Os demais modulos serao adicionados nas proximas fases.</p>
 */

public enum AgentType {
    /**
     * Agente especialista em planilhas, formulas e analise de dados no Excel.
     */
    EXCEL("EXCEL", "Especialista em Excel");
    
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