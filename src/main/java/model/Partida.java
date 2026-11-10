package model;

public class Partida {
    private int idPartida;
    private Jogador jogador1;
    private Jogador jogador2;
    private Jogador jogadorDaVez;
    private Categoria categoriaRodadaAtual;
    private int perguntasRespondidasNaRodada; // Zera a cada nova rodada (máximo 3)
    private boolean finalizada;

    public Partida(){}

    public Partida(int idPartida, Jogador jogador1, Jogador jogador2, Jogador jogadorDaVez, Categoria categoriaRodadaAtual, int perguntasRespondidasNaRodada, boolean finalizada) {
        this.idPartida = idPartida;
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.jogadorDaVez = jogadorDaVez;
        this.categoriaRodadaAtual = categoriaRodadaAtual;
        this.perguntasRespondidasNaRodada = perguntasRespondidasNaRodada;
        this.finalizada = finalizada;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public Jogador getJogador1() {
        return jogador1;
    }

    public void setJogador1(Jogador jogador1) {
        this.jogador1 = jogador1;
    }

    public Jogador getJogador2() {
        return jogador2;
    }

    public void setJogador2(Jogador jogador2) {
        this.jogador2 = jogador2;
    }

    public Jogador getJogadorDaVez() {
        return jogadorDaVez;
    }

    public void setJogadorDaVez(Jogador jogadorDaVez) {
        this.jogadorDaVez = jogadorDaVez;
    }

    public Categoria getCategoriaRodadaAtual() {
        return categoriaRodadaAtual;
    }

    public void setCategoriaRodadaAtual(Categoria categoriaRodadaAtual) {
        this.categoriaRodadaAtual = categoriaRodadaAtual;
    }

    public int getPerguntasRespondidasNaRodada() {
        return perguntasRespondidasNaRodada;
    }

    public void setPerguntasRespondidasNaRodada(int perguntasRespondidasNaRodada) {
        this.perguntasRespondidasNaRodada = perguntasRespondidasNaRodada;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    // fazer os métodos de controle de turno (ex: alternarTurno(), incrementarPergunta())
}
