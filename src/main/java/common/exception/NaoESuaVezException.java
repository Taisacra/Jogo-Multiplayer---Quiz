package common.exception;

public class NaoESuaVezException extends QuizException{
    private static final long serialVersionUID = 1L;

    public NaoESuaVezException() {
        super("Não é a sua vez de jogar.");
    }
    public NaoESuaVezException(String message) {
        super(message);
    }

    public NaoESuaVezException(int id) {
        super("Aguarde: agora é a vez do jogador " + id );
    }
}
