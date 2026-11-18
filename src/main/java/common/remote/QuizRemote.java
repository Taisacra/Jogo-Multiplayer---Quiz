package common.remote;

import common.dto.EntradaDTO;
import common.dto.PerguntaDTO;
import common.dto.PlacarDTO;
import common.dto.ResultadoRespostaDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface QuizRemote extends Remote {

    // Entrada dos dois jogadores. O cliente entrega o seu callback: o 1º espera e é avisado por
    // callback (partidaIniciada) quando o 2º entra. Quem entra primeiro é sempre o jogador 1.
    EntradaDTO entrarNaPartida(String nomeJogador, ClienteCallback callback) throws RemoteException;

    PerguntaDTO getPerguntaAtual(int partidaId, int jogadorId) throws RemoteException;

    ResultadoRespostaDTO responder(int partidaId, int jogadorId, int perguntaId, char alternativa)
            throws RemoteException;

    // Consultas pontuais (o cliente não precisa fazer polling: recebe os avisos por callback)
    PlacarDTO getPlacar(int partidaId) throws RemoteException;

    boolean isFinalizada(int partidaId) throws RemoteException;

    // Nome do vencedor, ou null se a partida ainda não terminou
    String getVencedor(int partidaId) throws RemoteException;

    // Sinal de vida: cada cliente chama a cada ~2 s durante a partida. Sem sinal por 10 s = desconectado
    void ping(int partidaId, int jogadorId) throws RemoteException;

    // Saída voluntária (ex.: fechou a janela): a partida termina e o adversário vence
    void sairDaPartida(int partidaId, int jogadorId) throws RemoteException;

    // Motivo do fim quando foi por desconexão; null se a partida não terminou ou terminou normalmente
    String getMotivoFim(int partidaId) throws RemoteException;
}