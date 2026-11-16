package server;

import util.ConexaoH2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Ponto de partida do servidor: prepara o banco, cria o objeto remoto e o publica no RMI Registry.
 *
 * Uso:
 *   - na mesma máquina:        executar sem argumentos;
 *   - em duas máquinas (rede): passar o IP do servidor como 1º argumento, por exemplo 192.168.0.10
 *     (IntelliJ: Run > Edit Configurations > Program arguments).
 */
public class ServidorMain {

    //Porta do RMI Registry
    public static final int PORTA_REGISTRY = 1099;

    //Nome que o objeto remoto fica registrado - o cliente usa o mesmo nome no lookup.
    public static final String NOME_SERVICO = "QuizRemote";

    //O Registry e o objeto remoto precisam de uma referência forte (static): sem ela o coletor de lixo
    //poderia descartá-los, e o servidor pararia de responder sem nenhum erro.
    private static Registry registry;
    private static QuizRemoteImpl quiz;

    public static void main(String[] args) {
        try {
            // 1) IP que será embutido no stub entregue aos clientes. Precisa ser definido ANTES
            //    de exportar qualquer objeto remoto (o Registry e o QuizRemoteImpl). Sem isso, em
            //    duas máquinas, o stub pode apontar para 127.0.0.1 e o cliente não conecta.
            if (args.length > 0) {
                System.setProperty("java.rmi.server.hostname", args[0]);
            }

            //Banco primeiro: o PartidaService (criado dentro do QuizRemoteImpl) já usa o banco
            ConexaoH2.inicializarBanco();

            //Cria o objeto remoto; ele é exportado na porta 1100
            quiz = new QuizRemoteImpl();

            //Cria o Registry na porta 1099 e registra o objeto (bind) para os clientes encontrarem
            registry = LocateRegistry.createRegistry(PORTA_REGISTRY);
            registry.rebind(NOME_SERVICO, quiz);

            System.out.println("Servidor do QUIZ no ar.");
            System.out.println("  RMI Registry: porta " + PORTA_REGISTRY + " (nome \"" + NOME_SERVICO + "\")");
            System.out.println("  Objeto remoto: porta " + QuizRemoteImpl.PORTA_OBJETO);
            System.out.println("  IP anunciado aos clientes: "
                    + System.getProperty("java.rmi.server.hostname", "(não definido: serve só para testes na mesma máquina)"));
            // Não há mais nada a fazer: as threads do RMI mantêm o servidor ativo.

        } catch (Exception e) {
            System.err.println("Falha ao iniciar o servidor. Se a porta " + PORTA_REGISTRY
                    + " ou " + QuizRemoteImpl.PORTA_OBJETO + " já está em uso, há outro servidor aberto.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}