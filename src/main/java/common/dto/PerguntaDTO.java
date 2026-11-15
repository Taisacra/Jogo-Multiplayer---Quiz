package common.dto;

import java.io.Serializable;
import java.util.List;

public class PerguntaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String categoria;
    private String enunciado;
    private List<String> alternativas;
    private int segundosRestantes; // tempo que falta para pergunta expirar

    public PerguntaDTO(int id, String categoria, String enunciado, List<String> alternativas, int segundosRestantes) {
        this.id = id;
        this.categoria = categoria;
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.segundosRestantes = segundosRestantes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public List<String> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<String> alternativas) {
        this.alternativas = alternativas;
    }

    public int getSegundosRestantes() {  return segundosRestantes;}

    public void setSegundosRestantes(int segundosRestantes) { this.segundosRestantes = segundosRestantes; }
}
