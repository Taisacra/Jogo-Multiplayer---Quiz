package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.dto.EntradaDTO;
import common.dto.PerguntaDTO;
import common.dto.PlacarDTO;
import common.dto.ResultadoRespostaDTO;
import common.exception.*;
import common.remote.ClienteCallback;
import common.remote.QuizRemote;
import model.Jogador;
import model.Partida;
import service.PartidaListener;
import service.PartidaService;

/**
 * Objeto remoto do jogo: é a "porta de entrada" que os clientes enxergam pela rede.
 *
 * Ela faz três coisas:
 *  1. controla a fila de entrada (pareia o 1º e o 2º jogador numa partida);
 *  2. repassa as demais chamadas ao PartidaService, que concentra as regras e o relógio.
 *	3. avisa os clientes por Callback quando algo muda - implementa o partidaListener
 *
 *
 * ATENÇÃO: o PartidaService é criado aqui (campo "service"), e o construtor dele limpa o
 * histórico no banco. Por isso o banco precisa estar inicializado ANTES de criar esta classe
 * (ConexaoH2.inicializarBanco() no ServidorMain).
 */

public class QuizRemoteImpl extends UnicastRemoteObject implements QuizRemote, PartidaListener{

    private static final long serialVersionUID = 1L;

    //Porta fixa do objeto remoto
    public static final int PORTA_OBJETO = 1100;

    private final PartidaService service = new PartidaService();

    //Fila de entrada

    private static final class Espera{
        final String nome;
        final ClienteCallback callback;

        Espera (String nome, ClienteCallback callback){
            this.nome = nome;
            this.callback = callback;
        }

    }

    //Nome do 1o jogador -- null se ninguem espera
    private Espera espera;

    //Callbacks das partidas em andamento: partidaId -> (jogadorId -> callback do cliente)
    private final transient Map<Integer, Map<Integer, ClienteCallback>> callbacks = new ConcurrentHashMap<>();

    // Uma thread por partida, para os avisos saírem na ordem e um cliente lento não atrasar as outras partidas
    private final transient Map<Integer, ExecutorService> notificadores = new ConcurrentHashMap<>();

    public QuizRemoteImpl() throws RemoteException{
        super(PORTA_OBJETO);
        service.setOuvinte(this);
    }

    //Entrada: 
    @Override
    public EntradaDTO entrarNaPartida(String nomeJogador, ClienteCallback callback) throws DadosInvalidosException {

        String nome = validarNome(nomeJogador);
        if (callback == null) {
            throw new DadosInvalidosException();
        }
        Espera novo = new Espera(nome, callback);

        while (true) {

            Espera primeiro;

            synchronized (this) {
                if (espera == null) {
                    // Ninguém esperando: este jogador é o 1º. O aviso de início chegará por callback.
                    espera = novo;
                    return new EntradaDTO(0, 1, false); // partidaId 0 = ainda não existe partida
                }
                primeiro = espera; // retira o 1º da fila
                espera = null;
            }

            // Fora do bloqueio (a chamada é de rede): confere se o 1º ainda está conectado
            if (estaConectado(primeiro.callback)) {
                return iniciarPartida(primeiro, novo);
            }
            // O 1º sumiu enquanto esperava: descarta e tenta de novo (este jogador pode virar o 1º)
            System.out.println("Jogador \"" + primeiro.nome + "\" saiu da fila sem avisar e foi descartado.");
        }
    }


    private EntradaDTO iniciarPartida(Espera primeiro, Espera segundo) throws ErroInternoException {
        Partida partida = service.criarPartida(primeiro.nome, segundo.nome);
        int partidaId = partida.getIdPartida();

        Map<Integer, ClienteCallback> daPartida = new ConcurrentHashMap<>();
        daPartida.put(1, primeiro.callback); // quem entrou primeiro é o jogador 1
        daPartida.put(2, segundo.callback);
        callbacks.put(partidaId, daPartida);

        System.out.println("Partida " + partidaId + " criada: " + primeiro.nome + " x " + segundo.nome
                + ". Quem começa: " + partida.getJogadorDaVez().getNome());

        // Os dois recebem partidaIniciada e depois o 1º placar, por callback e nessa ordem
        executorDa(partidaId).execute(() -> avisarInicio(partidaId));

        return new EntradaDTO(partidaId, 2, true);
    }

    //Repassa para o service

    @Override
    public PerguntaDTO getPerguntaAtual(int partidaId, int jogadorId) throws NaoESuaVezException, PartidaNaoEncontradaException, PartidaEncerradaException {
        return service.getPerguntaAtual(partidaId, jogadorId);
    }

    @Override
    public ResultadoRespostaDTO responder(int partidaId, int jogadorId, int perguntaId, char alternativa) throws NaoESuaVezException, PartidaNaoEncontradaException, PerguntaInvalidaException, PartidaEncerradaException {
        return service.responder(partidaId, jogadorId, perguntaId, alternativa);
    }

    @Override
    public PlacarDTO getPlacar(int partidaId) throws PartidaNaoEncontradaException {
        return service.getPlacar(partidaId);
    }

    @Override
    public boolean isFinalizada(int partidaId) throws PartidaNaoEncontradaException {
        return service.isFinalizada(partidaId);
    }

    @Override
    public String getVencedor(int partidaId) {
        Jogador vencedor = service.getVencedor(partidaId);
        return vencedor == null ? null : vencedor.getNome();
    }

    @Override
    public void ping(int partidaId, int jogadorId) throws PartidaNaoEncontradaException, JogadorInvalidoException {
        service.registrarSinal(partidaId, jogadorId);
    }

    @Override
    public void sairDaPartida(int partidaId, int jogadorId) throws PartidaNaoEncontradaException, JogadorInvalidoException {
        service.sairDaPartida(partidaId, jogadorId);
    }

    @Override
    public String getMotivoFim(int partidaId) throws PartidaNaoEncontradaException {
        return service.getMotivoFim(partidaId);
    }

    //Eventos do Service
    //Chamados de DENTRO do bloqueio do service: só enfileiram e voltam na hora.

    @Override
    public void placarAtualizado(int partidaId) {
        try {
            executorDa(partidaId).execute(() -> enviarPlacar(partidaId));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void partidaEncerrada(int partidaId) {
        // remove o executor do mapa: depois do aviso de fim, a thread da partida é encerrada
        ExecutorService executor = notificadores.remove(partidaId);
        if (executor == null) {
            return; // nenhum cliente foi registrado (ex.: a partida terminou ao ser criada)
        }
        try {
            executor.execute(() -> enviarFim(partidaId));
            executor.shutdown(); // conclui o que já está na fila (inclusive o aviso de fim) e encerra
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //avisos aos clientes (threads por partida)

    private void avisarInicio(int partidaId) throws PartidaNaoEncontradaException, JogadorInvalidoException {
        Map<Integer, ClienteCallback> daPartida = callbacks.get(partidaId);
        if (daPartida == null) {
            return;
        }
        for (Map.Entry<Integer, ClienteCallback> jogador : daPartida.entrySet()) {
            try {
                jogador.getValue().partidaIniciada(partidaId, jogador.getKey());
            } catch (RemoteException e) {
                falhaNoCallback(partidaId, jogador.getKey());
            }
        }
        enviarPlacar(partidaId);
    }

    private void enviarPlacar(int partidaId) throws PartidaNaoEncontradaException, JogadorInvalidoException {
        Map<Integer, ClienteCallback> daPartida = callbacks.get(partidaId);
        if (daPartida == null) {
            return;
        }
        PlacarDTO placar = service.getPlacar(partidaId);
        for (Map.Entry<Integer, ClienteCallback> jogador : daPartida.entrySet()) {
            try {
                jogador.getValue().placarAtualizado(partidaId, placar);
            } catch (RemoteException e) {
                falhaNoCallback(partidaId, jogador.getKey());
            }
        }
    }

    private void enviarFim(int partidaId) throws PartidaNaoEncontradaException {
        Map<Integer, ClienteCallback> daPartida = callbacks.remove(partidaId);
        if (daPartida == null) {
            return;
        }
        PlacarDTO placar = service.getPlacar(partidaId);
        Jogador vencedor = service.getVencedor(partidaId);
        String nomeVencedor = vencedor == null ? null : vencedor.getNome();
        String motivo = service.getMotivoFim(partidaId);

        for (ClienteCallback callback : daPartida.values()) {
            try {
                // o placar final vai junto: um placarAtualizado separado faria o cliente da vez pedir pergunta
                callback.partidaEncerrada(partidaId, placar, nomeVencedor, motivo);
            } catch (RemoteException e) {
                // a partida já terminou; quem não responde simplesmente não fica sabendo
            }
        }
    }

    /**
     * Um cliente não atendeu a chamada do servidor: ele caiu. Tira o callback dele (para não esperar
     * de novo) e encerra a partida, dando a vitória ao adversário.
     */
    private void falhaNoCallback(int partidaId, int jogadorId) throws PartidaNaoEncontradaException, JogadorInvalidoException {
        Map<Integer, ClienteCallback> daPartida = callbacks.get(partidaId);
        if (daPartida != null) {
            daPartida.remove(jogadorId);
        }
        System.out.println("Jogador " + jogadorId + " da partida " + partidaId
                + " não respondeu ao callback: desconectado.");
        try {
            service.sairDaPartida(partidaId, jogadorId);
        } catch (RuntimeException e) {
            // partida inexistente ou já encerrada: nada a fazer
        }
    }


    //Métodos de apoio

    private ExecutorService executorDa(int partidaId) {
        return notificadores.computeIfAbsent(partidaId, id -> Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "notificador-partida-" + id);
            t.setDaemon(true); // não impede a JVM de encerrar
            return t;
        }));
    }

    private static boolean estaConectado(ClienteCallback callback) {
        try {
            callback.verificarConexao();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    private static String validarNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DadosInvalidosException();
        }
        return nome.trim();
    }

}