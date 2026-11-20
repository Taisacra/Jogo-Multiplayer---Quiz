package common.exception;

public class PartidaEncerradaException extends QuizException{
    private static final long serialVersionUID = 1L;

    public PartidaEncerradaException() {
        super("A partida já foi encerrada.");
    }

    public PartidaEncerradaException(String message) {
        super(message);
    }

    public PartidaEncerradaException(int id) {
        super("Partida " + id + " já foi encerrada.");
    }
}
