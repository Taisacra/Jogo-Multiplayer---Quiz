package client;

import common.dto.PlacarDTO;
import common.remote.ClienteCallback;

import javax.swing.SwingUtilities;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Objeto remoto do CLIENTE: recebe as chamadas (callback) do servidor.
 *
 * As chamadas chegam em threads do RMI, não na thread do Swing. Por isso cada aviso é repassado
 * à tela com SwingUtilities.invokeLater, que mantém a ordem em que os avisos chegaram.
 *
 * Em DOIS COMPUTADORES o servidor precisa alcançar este objeto:
 *  - chame definirIpLocal(ipDestaMaquina) ANTES de criar o ClienteCallbackImpl;
 *  - libere a porta (padrão 1101) no firewall deste computador.
 * Dois clientes no MESMO computador não podem usar a mesma porta: passe portas diferentes.
 */
public class ClienteCallbackImpl extends UnicastRemoteObject implements ClienteCallback {

    private static final long serialVersionUID = 1L;

    /** Porta do callback (figura 1 do relatório). */
    public static final int PORTA_PADRAO = 1101;

    private final transient OuvinteDoJogo ouvinte;

    public ClienteCallbackImpl(OuvinteDoJogo ouvinte) throws RemoteException {
        this(PORTA_PADRAO, ouvinte);
    }

    public ClienteCallbackImpl(int porta, OuvinteDoJogo ouvinte) throws RemoteException {
        super(porta); // exporta o objeto nesta porta
        this.ouvinte = ouvinte;
    }

    /** IP deste computador, que o servidor usará para chamar o cliente. Chamar antes de criar o objeto. */
    public static void definirIpLocal(String ip) {
        System.setProperty("java.rmi.server.hostname", ip);
    }

    @Override
    public void partidaIniciada(int partidaId, int jogadorId) {
        SwingUtilities.invokeLater(() -> ouvinte.partidaIniciada(partidaId, jogadorId));
    }

    @Override
    public void placarAtualizado(int partidaId, PlacarDTO placar) {
        SwingUtilities.invokeLater(() -> ouvinte.placarAtualizado(partidaId, placar));
    }

    @Override
    public void partidaEncerrada(int partidaId, PlacarDTO placarFinal, String vencedor, String motivo) {
        SwingUtilities.invokeLater(() -> ouvinte.partidaEncerrada(partidaId, placarFinal, vencedor, motivo));
    }

    @Override
    public void verificarConexao() {
        // Propositalmente vazio e imediato: basta a chamada chegar para provar que o cliente está vivo
    }
}