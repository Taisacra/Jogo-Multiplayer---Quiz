package common.remote;

import common.dto.EntradaDTO;
import common.dto.PerguntaDTO;
import common.dto.PlacarDTO;
import common.dto.ResultadoRespostaDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface QuizRemote extends Remote {
    // Entrada dos dois jogadores: o primeiro espera, o segundo completa a dupla
    EntradaDTO entrarNaPartida(String nomeJogador) throws RemoteException;

    // O primeiro jogador consulta periodicamente até o adversário entrar
    EntradaDTO consultarEntrada() throws RemoteException;

    PerguntaDTO getPerguntaAtual(int partidaId) throws RemoteException;

    ResultadoRespostaDTO responder(int partidaId, int jogadorId, int perguntaId, char alternativa)
            throws RemoteException;

    PlacarDTO getPlacar(int partidaId) throws RemoteException;

    boolean isFinalizada(int partidaId) throws RemoteException;

    // Nome do vencedor, ou null se a partida ainda não terminou
    String getVencedor(int partidaId) throws RemoteException;

}
