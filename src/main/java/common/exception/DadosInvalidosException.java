package common.exception;

public class DadosInvalidosException extends QuizException{
    private static final long serialVersionUID = 1L;

    public DadosInvalidosException() {
        super("Dados inválidos.");
    }
    public DadosInvalidosException(String message) {
        super(message);
    }
}
