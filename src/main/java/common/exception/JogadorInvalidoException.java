package common.exception;

public class JogadorInvalidoException extends QuizException{
    private static final long serialVersionUID = 1L;

    public JogadorInvalidoException() {
        super("Jogador inválido.");
    }
    public JogadorInvalidoException(String message) {
        super(message);
    }

    public JogadorInvalidoException(int id) {
        super("Jogador " + id + " não existe nesta partida.");
    }
}
