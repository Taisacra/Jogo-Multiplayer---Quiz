package model;

import java.util.HashSet;
import java.util.Set;

public class Jogador {
    private int idJogador;
    private String nome;
    private Set<Categoria> estrelasConquistadas; // O "arsenal" de até 5 estrelas

    public Jogador(){}

    public Jogador(int idJogador, String nome) {
        this.idJogador = idJogador;
        this.nome = nome;
        this.estrelasConquistadas = new HashSet<>();
    }

    public void adicionarEstrela(Categoria categoria) {
        this.estrelasConquistadas.add(categoria);
    }

    public int getId() {
        return idJogador;
    }

    public void setId(int idJogador) {
        this.idJogador = idJogador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Categoria> getEstrelasConquistadas() {
        return estrelasConquistadas;
    }

    public void setEstrelasConquistadas(Set<Categoria> estrelasConquistadas) {
        this.estrelasConquistadas = estrelasConquistadas;
    }
}
