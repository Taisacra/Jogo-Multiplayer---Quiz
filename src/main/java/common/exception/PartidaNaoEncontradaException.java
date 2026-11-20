package common.exception;

public class PartidaNaoEncontradaException extends QuizException{
    private static final long serialVersionUID = 1L;

    public PartidaNaoEncontradaException() {
        super("Partida não encontrada");
    }

    public PartidaNaoEncontradaException(String message) {
        super(message);
    }

    public PartidaNaoEncontradaException(int id) {
        super("Partida " + id + " não encontrada");
    }
}
