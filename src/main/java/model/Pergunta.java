package model;

public class Pergunta {
    private int idPergunta;
    private int categoriaId;
    private String enunciado;
    private String altA, altB, altC, altD;
    private char respostaCorreta;

    public Pergunta(){}

    public Pergunta(int idPergunta, int categoriaId, String enunciado, String altA, String altB, String altC, String altD, char respostaCorreta){
        this.idPergunta = idPergunta;
        this.categoriaId = categoriaId;
        this.enunciado = enunciado;
        this.altA = altA;
        this.altB = altB;
        this.altC = altC;
        this.altD = altD;
        this.respostaCorreta = respostaCorreta;
    }

    public int getIdPergunta() {
        return idPergunta;
    }

    public void setIdPergunta(int idPergunta) {
        this.idPergunta = idPergunta;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getAltA() {
        return altA;
    }

    public void setAltA(String altA) {
        this.altA = altA;
    }

    public String getAltB() {
        return altB;
    }

    public void setAltB(String altB) {
        this.altB = altB;
    }

    public String getAltC() {
        return altC;
    }

    public void setAltC(String altC) {
        this.altC = altC;
    }

    public String getAltD() {
        return altD;
    }

    public void setAltD(String altD) {
        this.altD = altD;
    }

    public char getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(char respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

}
