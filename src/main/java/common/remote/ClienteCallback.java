package common.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.dto.PlacarDTO;

/**
 * Métodos que o SERVIDOR chama no CLIENTE (callback). O cliente se registra ao entrar na
 * partida, entregando uma referência a um objeto que implementa esta interface.
 */
public interface ClienteCallback extends Remote{

	void partidaIniciada(int partidaId, int jogadorId) throws RemoteException;
	
	void placarAtualizado(int partidaId, PlacarDTO placar) throws RemoteException;
	
	void partidaEncerrada(int partidaId, PlacarDTO placarFinal, String vencedor, String motivo) throws RemoteException;

	void verificarConexao() throws RemoteException;
}
