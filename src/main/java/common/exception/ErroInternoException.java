package common.exception;

public class ErroInternoException extends QuizException{
    private static final long serialVersionUID = 1L;

    public ErroInternoException() {
        super("Erro interno do servidor. Tente novamente mais tarde");
    }
    public ErroInternoException(String message) {
        super(message);
    }
}
