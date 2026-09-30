// URL DA API vem da variavel de ambiente do VITE
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

/**
 * Envia uma mensagem para o back-end e retorna a resposta do agente.
 * 
 * @param {string} sessionId - ID da sessao de conversa
 * @param {string} message - Mensagem do usuario
 * @returns {Promise<Object>} Resposta com sessionId, agentUsed, response e timestap
 */
export async function sendMessage(sessionId, message) {
    const resposta = await fetch(`${API_URL}/api/chat`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ sessionId, message }),
    })

    if (!resposta.ok) {
        const erro = await resposta.text()
        throw new Error(`Erro ${resposta.status}: ${erro}`)
    }
    return resposta.json()
}