package common.exception;

public class PerguntaInvalidaException extends QuizException{
    private static final long serialVersionUID = 1L;

    public PerguntaInvalidaException() {
        super("Pergunta inválida.");
    }
    public PerguntaInvalidaException(String message) {
        super(message);
    }

    public PerguntaInvalidaException(int id) {
        super("Esta não é a pergunta atual da partida. A pergunta" + id + " não é uma pergunta atual da rodada.");
    }
}
