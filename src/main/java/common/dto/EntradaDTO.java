package common.dto;

import java.io.Serializable;

public class EntradaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int partidaId;
    private int jogadorId;
    private boolean partidaIniciada;

    public EntradaDTO() {}

    public EntradaDTO(int partidaId, int jogadorId, boolean partidaIniciada) {
        this.partidaId = partidaId;
        this.jogadorId = jogadorId;
        this.partidaIniciada = partidaIniciada;
    }

    public int getPartidaId() {
        return partidaId;
    }

    public void setPartidaId(int partidaId) {
        this.partidaId = partidaId;
    }

    public int getJogadorId() {
        return jogadorId;
    }

    public void setJogadorId(int jogadorId) {
        this.jogadorId = jogadorId;
    }

    public boolean isPartidaIniciada() {
        return partidaIniciada;
    }

    public void setPartidaIniciada(boolean partidaIniciada) {
        this.partidaIniciada = partidaIniciada;
    }
}
