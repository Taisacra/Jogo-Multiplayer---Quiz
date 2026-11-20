package common.exception;

public class QuizException extends Exception {
    private static final long serialVersionUID = 1L;

    public QuizException() {
        super("Erro no quiz");
    }

    public QuizException(String message) {
        super(message);
    }
}
