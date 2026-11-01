package common.dto;

import java.io.Serializable;
import java.util.List;

public class PlacarDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeJogador1;
    private List<String> estrelasJogador1; // Nomes das categorias conquistadas

    private String nomeJogador2;
    private List<String> estrelasJogador2;

    private String nomeJogadorDaVez;

    public PlacarDTO(){}

    public PlacarDTO(String nomeJogador1, List<String> estrelasJogador1, String nomeJogador2, List<String> estrelasJogador2, String nomeJogadorDaVez) {
        this.nomeJogador1 = nomeJogador1;
        this.estrelasJogador1 = estrelasJogador1;
        this.nomeJogador2 = nomeJogador2;
        this.estrelasJogador2 = estrelasJogador2;
        this.nomeJogadorDaVez = nomeJogadorDaVez;
    }

    public String getNomeJogador1() {
        return nomeJogador1;
    }

    public void setNomeJogador1(String nomeJogador1) {
        this.nomeJogador1 = nomeJogador1;
    }

    public List<String> getEstrelasJogador1() {
        return estrelasJogador1;
    }

    public void setEstrelasJogador1(List<String> estrelasJogador1) {
        this.estrelasJogador1 = estrelasJogador1;
    }

    public String getNomeJogador2() {
        return nomeJogador2;
    }

    public void setNomeJogador2(String nomeJogador2) {
        this.nomeJogador2 = nomeJogador2;
    }

    public List<String> getEstrelasJogador2() {
        return estrelasJogador2;
    }

    public void setEstrelasJogador2(List<String> estrelasJogador2) {
        this.estrelasJogador2 = estrelasJogador2;
    }

    public String getNomeJogadorDaVez() {
        return nomeJogadorDaVez;
    }

    public void setNomeJogadorDaVez(String nomeJogadorDaVez) {
        this.nomeJogadorDaVez = nomeJogadorDaVez;
    }
}
