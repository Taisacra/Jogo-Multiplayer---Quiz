package client;

import common.dto.PlacarDTO;

/**
 * O que a tela do jogo precisa reagir. É chamada na thread de eventos do Swing (EDT),
 * então pode atualizar componentes diretamente.
 */
public interface OuvinteDoJogo {

    void partidaIniciada(int partidaId, int jogadorId);

    void placarAtualizado(int partidaId, PlacarDTO placar);

    void partidaEncerrada(int partidaId, PlacarDTO placarFinal, String vencedor, String motivo);
}