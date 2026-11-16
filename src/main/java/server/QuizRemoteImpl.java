package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.dto.EntradaDTO;
import common.dto.PerguntaDTO;
import common.dto.PlacarDTO;
import common.dto.ResultadoRespostaDTO;
import common.remote.QuizRemote;
import model.Jogador;
import model.Partida;
import service.PartidaService;

/**
 * Objeto remoto do jogo: é a "porta de entrada" que os clientes enxergam pela rede.
 *
 * Esta classe não tem regra de jogo. Ela faz duas coisas:
 *  1. controla a fila de entrada (pareia o 1º e o 2º jogador numa partida);
 *  2. repassa as demais chamadas ao PartidaService, que concentra as regras e o relógio.
 *
 * ATENÇÃO: o PartidaService é criado aqui (campo "service"), e o construtor dele limpa o
 * histórico no banco. Por isso o banco precisa estar inicializado ANTES de criar esta classe
 * (ConexaoH2.inicializarBanco() no ServidorMain).
 */

public class QuizRemoteImpl extends UnicastRemoteObject implements QuizRemote{

	private static final long serialVersionUID = 1L;
	
	//Porta fixa do objeto remoto
	public static final int PORTA_OBJETO = 1100;
	
	
    /**
     * Tempo máximo que o servidor espera por sinal do jogador que está na fila. O cliente do
     * 1º jogador consulta a cada 1 ou 2 segundos (consultarEntrada); se passar este tempo sem
     * nenhuma consulta, ele é considerado desistente e a vaga é liberada.
     */

	private static final long TEMPO_MAXIMO_SEM_SINAL_MS = 10_000;
	
	private final PartidaService service = new PartidaService();
	
	//Fila de entrada
	
	//Nome do 1o jogador -- null se ninguem espera
	private String nomeEmEspera;

	//Ultima vez que o 1o jogador deu sinal -- entrou ou consultou
	private long ultimoSinalDoPrimeiro;
	
	//Preenchida quando o 2o jogador entra -- fica guardada ate o 1o jogador retira-la
	private EntradaDTO entradaDoPrimeiro;
	private long entradaDoPrimeiroCriadaEm;
	
    public QuizRemoteImpl() throws RemoteException {
        super(PORTA_OBJETO); // exporta o objeto na porta fixa 1100
    }
    
    //Entrada: 
    @Override
    public synchronized EntradaDTO entrarNaPartida(String nomeJogador) {
    	String nome = validarNome(nomeJogador);
    	
    	//Descarta caso o jogador desista
    	if (entradaDoPrimeiro != null && passouDoLimite(entradaDoPrimeiroCriadaEm)) {
            entradaDoPrimeiro = null;
        }
    	
    	//Ainda ha um par esperando o 1o jogador retirar a entrada
    	if (entradaDoPrimeiro != null) {
            throw new IllegalStateException("Já existe uma partida sendo formada. Tente novamente em instantes.");
        }
    	
    	// O 1º jogador da fila parou de consultar: desistiu, então a vaga é liberada
        if (nomeEmEspera != null && passouDoLimite(ultimoSinalDoPrimeiro)) {
            nomeEmEspera = null;
        }
        
        //Ninguém esperando: este jogador é o 1º e fica aguardando o adversário
        if (nomeEmEspera == null) {
            nomeEmEspera = nome;
            ultimoSinalDoPrimeiro = System.nanoTime();
            return new EntradaDTO(0, 1, false); // partidaId 0 - ainda nao comeca a partida
        }
        
        //Já existe o 1o jogador esperando, este é o 2o jogador, e a partida pode começar
        Partida partida = service.criarPartida(nomeEmEspera, nome);
        int partidaId = partida.getIdPartida();
        System.out.println("Partida " + partidaId + " criada: " + nomeEmEspera + " x " + nome
                + ". Quem começa: " + partida.getJogadorDaVez().getNome());
        
        //Guarda a entrada do 1o jogador, que a retira em consultarEntrada()
        entradaDoPrimeiro = new EntradaDTO(partidaId, 1, true);
        entradaDoPrimeiroCriadaEm = System.nanoTime();
        nomeEmEspera = null;

        return new EntradaDTO(partidaId, 2, true);
    }
    
    
    @Override
    public synchronized EntradaDTO consultarEntrada() {
        //O adversário já entrou - entrega os dados da partida (uma única vez)
        if (entradaDoPrimeiro != null) {
            EntradaDTO entrada = entradaDoPrimeiro;
            entradaDoPrimeiro = null;
            return entrada;
        }

        // Ainda esperando: a consulta serve também como sinal de vida do 1º jogador
        if (nomeEmEspera != null) {
            ultimoSinalDoPrimeiro = System.nanoTime();
        }
        return new EntradaDTO(0, 1, false);
    }
    
    
    //Repassa para o service
 
    @Override
    public PerguntaDTO getPerguntaAtual(int partidaId, int jogadorId) {
        return service.getPerguntaAtual(partidaId, jogadorId);
    }

    @Override
    public ResultadoRespostaDTO responder(int partidaId, int jogadorId, int perguntaId, char alternativa) {
        return service.responder(partidaId, jogadorId, perguntaId, alternativa);
    }

    @Override
    public PlacarDTO getPlacar(int partidaId) {
        return service.getPlacar(partidaId);
    }

    @Override
    public boolean isFinalizada(int partidaId) {
        return service.isFinalizada(partidaId);
    }

    @Override
    public String getVencedor(int partidaId) {
        Jogador vencedor = service.getVencedor(partidaId);
        return vencedor == null ? null : vencedor.getNome();
    }
    
    
    //Métodos de validacao
    private static String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do jogador.");
        }
        return nome.trim();
    }

    private static boolean passouDoLimite(long instanteEmNanos) {
        long decorridoMs = (System.nanoTime() - instanteEmNanos) / 1_000_000;
        return decorridoMs > TEMPO_MAXIMO_SEM_SINAL_MS;
    }
    
}
