package common.dto;

import java.io.Serializable;

public class ResultadoRespostaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean acertou;
    private boolean rodadaEncerrada;
    private boolean estrelaConquistada;
    private boolean vitoria;
    private String mensagem;

    public ResultadoRespostaDTO(){}

    public ResultadoRespostaDTO(boolean acertou, boolean rodadaEncerrada, boolean estrelaConquistada, boolean vitoria, String mensagem) {
        this.acertou = acertou;
        this.rodadaEncerrada = rodadaEncerrada;
        this.estrelaConquistada = estrelaConquistada;
        this.vitoria = vitoria;
        this.mensagem = mensagem;
    }

    public boolean isAcertou() {
        return acertou;
    }

    public void setAcertou(boolean acertou) {
        this.acertou = acertou;
    }

    public boolean isRodadaEncerrada() {
        return rodadaEncerrada;
    }

    public void setRodadaEncerrada(boolean rodadaEncerrada) {
        this.rodadaEncerrada = rodadaEncerrada;
    }

    public boolean isEstrelaConquistada() {
        return estrelaConquistada;
    }

    public void setEstrelaConquistada(boolean estrelaConquistada) {
        this.estrelaConquistada = estrelaConquistada;
    }

    public boolean isVitoria() {
        return vitoria;
    }

    public void setVitoria(boolean vitoria) {
        this.vitoria = vitoria;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
