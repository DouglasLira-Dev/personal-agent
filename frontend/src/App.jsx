import { useState } from 'react'
import { sendMessage } from './services/api'
import './App.css'

function App() {
  // ID unico da sessao, gerado uma vez por conversa
  const [sessionId] = useState(() => crypto.randomUUID())

  // Historico de mensagens
  const [mensagens, setMensagens] = useState([])

  // Texto atual do campo de entrada
  const [textoAtual, setTextoAtual] = useState('')

  // Indica que a resposta esta sendo processada
  const [carregando, setCarregando] = useState(false)

  // Envia a mensagem do usuario e processa a resposta
  async function enviarMensagem(evento) {
    evento.preventDefault()

    const textoLimpo = textoAtual.trim()
    if (!textoLimpo || carregando) return

    // Adiciona a mensagem do usuario na Lista
    setMensagens((anteriores) => [
      ...anteriores,
      { autor: 'usuario', conteudo: textoLimpo },
    ])

    setTextoAtual('')
    setCarregando(true)

    try {
      const resposta = await sendMessage(sessionId, textoLimpo)

      // Adiciona a resposta do agente na Lista
      setMensagens((anteriores) => [
        ...anteriores,
        { autor: 'agente', conteudo: resposta.response },
      ])
    } catch (erro) {
      setMensagens((anteriores) => [
        ...anteriores,
        { autor: 'erro', conteudo: `Falha ao enviar: ${erro.message}` },
      ])
    } finally {
      setCarregando(false)
    }
  }
  return (
    <div className="chat-container">
      <header className="chat-header">
        <h1>Agente Excel</h1>
        <small>Sessao: {sessionId.slice(0, 8)}</small>
      </header>

      <main className="chat-mensagens">
        {mensagens.length === 0 && (
          <p className="chat-vazio">
            Faca uma pergunta sobre Excel para comecar.
          </p>
        )}

        {mensagens.map((msg, indice) => (
          <div key={indice} className={`mensagem mensagem-${msg.autor}`}>
            <strong>
              {msg.autor === 'usuario' ? 'Voce' : msg.autor === 'agente' ? 'Agente' : 'Erro'}:
            </strong>
            <p>{msg.conteudo}</p>
          </div>
        ))}

        {carregando && (
          <div className="mensagem mensagem-agente">
            <em>Digitando...</em>
          </div>
        )}
      </main>

      <form className="chat-form" onSubmit={enviarMensagem}>
        <input
          type="text"
          value={textoAtual}
          onChange={(e) => setTextoAtual(e.target.value)}
          placeholder="Digite sua pergunta..."
          disabled={carregando}
          />

        <button type="submit" disabled={carregando || !textoAtual.trim()}>
          {carregando ? 'Enviando...' : 'Enviar'}
        </button>
      </form>
    </div>
  )
}

export default App