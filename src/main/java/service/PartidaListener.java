package service;

/**
 * Ouvinte dos eventos de uma partida. O PartidaService não conhece RMI: ele só avisa este ouvinte,
 * e quem implementa (o servidor) decide como avisar os clientes.
 *
 * ATENÇÃO: os métodos são chamados de DENTRO de blocos synchronized do serviço. A implementação
 * deve apenas enfileirar o aviso e voltar na hora, nunca fazer chamadas de rede aqui.
 */
public interface PartidaListener {

    /** O estado da partida mudou (placar, vez, rodada) e ela continua em andamento. */
    void placarAtualizado(int partidaId);

    /** A partida terminou (vitória, derrota por falta de perguntas ou desconexão). */
    void partidaEncerrada(int partidaId);
}