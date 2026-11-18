package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import common.dto.PerguntaDTO;
import common.dto.PlacarDTO;
import common.dto.ResultadoRespostaDTO;
import dao.CategoriaDAO;
import dao.HistoricoRespostaDAO;
import dao.PerguntaDAO;
import model.Categoria;
import model.Jogador;
import model.Partida;
import model.Pergunta;


/**
 * Todos os métodos públicos são synchronized, pois os dois clientes chamam o servidor
 * em threads diferentes.
 *
 * O estado da rodada (as 3 perguntas sorteadas) fica guardado neste serviço, de modo
 * que as classes Partida, Jogador e os DTOs são usados exatamente como estão.
 *
 * CONTROLE DE TEMPO: cada pergunta tem um limite de SEGUNDOS_POR_PERGUNTA. O relógio
 * roda aqui no servidor (o cliente só exibe a contagem) e começa quando o jogador da
 * vez busca a pergunta (getPerguntaAtual). Se o tempo esgotar, o serviço trata como
 * resposta errada e passa a vez ao adversário.
 *
 * * DESCONEXÃO: o servidor não é avisado quando um cliente cai, então cada cliente envia um
 * sinal de vida (registrarSinal) a cada poucos segundos. Uma tarefa periódica (verificarConexoes) encerra a partida, dando a vitória ao adversário, se um jogador ficar mais de
 * SEGUNDOS_SEM_SINAL_PARA_DESCONECTAR sem dar sinal. Quem sai de propósito chama sairDaPartida,
 * e o fim é imediato.
 *
 */

public class PartidaService {

	private final CategoriaDAO categoriaDAO = new CategoriaDAO();
	private final PerguntaDAO perguntaDAO = new PerguntaDAO();
	private final HistoricoRespostaDAO historicoDAO = new HistoricoRespostaDAO();

	private final Map<Integer, Partida> partidas = new HashMap<>();
	private final Map<Integer, List<Pergunta>> perguntasDaRodada = new HashMap<>();
	private final Map<Integer, Jogador> vencedores = new HashMap<>();

	private List<Categoria> categorias;

	private static final int PERGUNTAS_POR_RODADA = 3;

	// ---- Controle de tempo ----
	private static final int SEGUNDOS_POR_PERGUNTA = 15;
	// Se o jogador da vez nunca abrir a pergunta (ex.: desconectou), esta é a espera máxima
	private static final int SEGUNDOS_MAXIMO_PARA_ABRIR = 30;
	// Folga para a latência da rede: uma resposta enviada no limite não é punida
	private static final long FOLGA_MS = 1000;

	// O "despertador" do servidor: executa tarefas agendadas em uma thread própria
	private final ScheduledExecutorService relogio = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "relogio-quiz");
		t.setDaemon(true); // não impede a JVM de encerrar
		return t;
	});

	// Para cada partida: o alarme agendado (para poder cancelar) e o horário limite
	private final Map<Integer, ScheduledFuture<?>> alarmes = new HashMap<>();
	private final Map<Integer, Long> prazos = new HashMap<>();

	//Controle de desconexão - sem sinal de vida por mais que isso é considerado desconectado
	private static final int SEGUNDOS_SEM_SINAL_PARA_DESCONECTAR = 10;
	// De quanto em quanto tempo o servidor confere os sinais
	private static final int INTERVALO_VIGIA_SEGUNDOS = 2;

	/** Para cada partida em andamento: jogadorId -> instante (System.nanoTime) do último sinal.
	 * A partida sai deste mapa quando termina.
	 */
	private final Map<Integer, Map<Integer, Long>> ultimosSinais = new HashMap<>();
	// Motivo do fim quando não foi o fim normal (hoje, só desconexão)
	private final Map<Integer, String> motivosFim = new HashMap<>();

	/**Aviso de eventos
	 *  O serviço não conhece RMI: só avisa este ouvinte (o servidor), que deve apenas enfileirar o
	 *  aviso e voltar, pois os avisos são disparados de dentro de blocos synchronized.
	 */

	private volatile PartidaListener ouvinte;

	public void setOuvinte(PartidaListener ouvinte) {
		this.ouvinte = ouvinte;
	}

	private final Random random = new Random();

	private int proximoIdPartida = 1;

	//Método para limpar os historicos de execucoes anteriores
	public PartidaService() {
		historicoDAO.limparTodo();
		// Liga o vigia de conexões: roda de tempos em tempos na mesma thread do relógio
		relogio.scheduleWithFixedDelay(this::verificarConexoes,
				INTERVALO_VIGIA_SEGUNDOS, INTERVALO_VIGIA_SEGUNDOS, TimeUnit.SECONDS);

	}


	//Método que cria a partida
	public synchronized Partida criarPartida(String nomeJogador1, String nomeJogador2) {
		List<Categoria> todas = categoriaDAO.buscarTodas();
		if (todas.isEmpty()) {
			throw new IllegalStateException("Nenhuma categoria cadastrada no banco de dados.");
		}

		categorias = todas;

		Jogador j1 = new Jogador(1, nomeJogador1);
		Jogador j2 = new Jogador(2, nomeJogador2);

		//Sorteio para ver quem começa
		Jogador primeiro = random.nextBoolean() ? j1 : j2;

		//Cria a partida e coloca no Map partidas
		Partida partida = new Partida(proximoIdPartida++, j1, j2, primeiro, null, 0, false);
		partidas.put(partida.getIdPartida(), partida);

		//Os dois jogadores começam com um sinal de vida "de agora" (tempo para o cliente iniciar o ping)
		Map<Integer, Long> sinais = new HashMap<>();
		sinais.put(j1.getId(), System.nanoTime());
		sinais.put(j2.getId(), System.nanoTime());
		ultimosSinais.put(partida.getIdPartida(), sinais);

		//Inicia a primeira rodada e, se ela foi formada, prepara a primeira pergunta
		boolean rodadaFormada = iniciarRodada(partida);
		if (rodadaFormada) {
			prepararPergunta(partida);
		}

		return partida;
	}


	//Método que controla toda a partida - processa a resposta e depois avisa os clientes da mudança
	public synchronized ResultadoRespostaDTO responder(int partidaId, int jogadorId, int perguntaId, char alternativa) {
		ResultadoRespostaDTO resultado = processarResposta(partidaId, jogadorId, perguntaId, alternativa);
		notificarMudanca(partidaId);
		return resultado;
	}

	//Regras da resposta (o corpo que antes ficava direto em responder)
	private ResultadoRespostaDTO processarResposta(int partidaId, int jogadorId, int perguntaId, char alternativa) {

		Partida partida = obterPartida(partidaId);
		if (partida.isFinalizada()) {
			throw new IllegalStateException("A partida já foi encerrada.");
		}

		marcarSinal(partidaId, jogadorId); //quem responde está claramente conectado

		Jogador jogador = partida.getJogadorDaVez();
		if (jogador.getId() != jogadorId) {
			throw new IllegalStateException("Não é a vez deste jogador.");
		}

		Pergunta atual = perguntaAtual(partida);
		if (atual.getIdPergunta() != perguntaId) {
			throw new IllegalArgumentException("Esta não é a pergunta atual da rodada.");
		}

		//Confere o prazo: se a resposta chegou depois do limite, conta como tempo esgotado
		Long prazo = prazos.get(partidaId);
		if (prazo != null && System.currentTimeMillis() > prazo + FOLGA_MS) {
			return processarErro(partida, jogador, "Tempo esgotado");
		}

		boolean acertou = Character.toUpperCase(alternativa) ==
				Character.toUpperCase(atual.getRespostaCorreta());

		//se errar
		if (!acertou) {
			return processarErro(partida, jogador, "Resposta errada");
		}

		//se acertar
		historicoDAO.registrarResposta(partida.getIdPartida(), jogador.getId(),
				atual.getIdPergunta(), true);

		int respondidas = partida.getPerguntasRespondidasNaRodada() + 1;
		partida.setPerguntasRespondidasNaRodada(respondidas);

		if (respondidas < PERGUNTAS_POR_RODADA) {
			prepararPergunta(partida); //a próxima pergunta só terá relógio quando o jogador a abrir
			return new ResultadoRespostaDTO(true, false, false, false, "Resposta correta!");
		}

		// se acertou as 3 perguntas da rodada: conquista a estrela da categoria
		Categoria conquistada = partida.getCategoriaRodadaAtual();
		if (!jaConquistou(jogador, conquistada)) {
			jogador.adicionarEstrela(conquistada);
		}

		//se ele conquistar todas as estrelas
		if (jogador.getEstrelasConquistadas().size() >= categorias.size()) {
			finalizar(partida, jogador);
			return new ResultadoRespostaDTO(true, true, true, true,
					"Você conquistou a estrela de " + conquistada.getNome() + " e venceu a partida!");
		}

		//se nao conquistou todas e continua jogando, em uma nova categoria
		boolean rodadaFormada = iniciarRodada(partida);
		if (rodadaFormada) {
			prepararPergunta(partida);
			return new ResultadoRespostaDTO(true, true, true, false,
					"Estrela de " + conquistada.getNome() + " conquistada! Nova rodada: "
							+ partida.getCategoriaRodadaAtual().getNome() + ".");
		}
		return new ResultadoRespostaDTO(true, true, true, false,
				"Estrela de " + conquistada.getNome() + " conquistada, mas não há 3 perguntas inéditas na próxima "
						+ "categoria sorteada. Você perdeu a partida.");
	}

	//Métodos de consulta
	/**
	 * Entrega a pergunta atual ao jogador da vez. É aqui que o relógio de 15 segundos
	 * começa: na primeira vez que o jogador busca a pergunta. Chamadas repetidas para a
	 * mesma pergunta só devolvem o tempo restante, sem reiniciar a contagem.
	 */
	public synchronized PerguntaDTO getPerguntaAtual(int partidaId, int jogadorId) {
		Partida partida = obterPartida(partidaId);
		if (partida.isFinalizada()) {
			throw new IllegalStateException("A partida já foi encerrada.");
		}
		if (partida.getJogadorDaVez().getId() != jogadorId) {
			throw new IllegalStateException("Não é a vez deste jogador.");
		}

		if (!prazos.containsKey(partidaId)) {
			iniciarRelogio(partida); //primeira abertura desta pergunta: começa a contagem
		}

		Pergunta p = perguntaAtual(partida);
		return new PerguntaDTO(p.getIdPergunta(), partida.getCategoriaRodadaAtual().getNome(), p.getEnunciado(),
				List.of(p.getAltA(), p.getAltB(), p.getAltC(), p.getAltD()),
				segundosRestantes(partidaId));
	}

	public synchronized PlacarDTO getPlacar(int partidaId) {
		Partida p = obterPartida(partidaId);
		return new PlacarDTO(
				p.getJogador1().getNome(), nomesDasEstrelas(p.getJogador1()),
				p.getJogador2().getNome(), nomesDasEstrelas(p.getJogador2()),
				p.getJogadorDaVez().getNome(), p.getJogadorDaVez().getId());
	}

	public synchronized Partida getPartida(int partidaId) {
		return obterPartida(partidaId);
	}

	public synchronized boolean isFinalizada(int partidaId) {
		return obterPartida(partidaId).isFinalizada();
	}

	// Vencedor da partida, ou null se ela ainda não terminou
	public synchronized Jogador getVencedor(int partidaId) {
		return vencedores.get(partidaId);
	}


	/**
	 * Motivo do fim da partida quando ela terminou por desconexão (ex.: "Bia desconectou. Ana venceu
	 * a partida."). Devolve null se a partida não terminou ou terminou normalmente.
	 */
	public synchronized String getMotivoFim(int partidaId) {
		obterPartida(partidaId);
		return motivosFim.get(partidaId);
	}

	/**
	 * Sinal de vida do cliente (ping). Chamado de tempos em tempos por cada jogador, o tempo todo,
	 * mesmo quando não é a vez dele. Depois do fim da partida o sinal é simplesmente ignorado.
	 */
	public synchronized void registrarSinal(int partidaId, int jogadorId) {
		Partida partida = obterPartida(partidaId);
		jogadorDaPartida(partida, jogadorId); //valida o id do jogador
		marcarSinal(partidaId, jogadorId);
	}

	/** Saída voluntária (ex.: fechou a janela): a partida termina e o adversário vence. */
	public synchronized void sairDaPartida(int partidaId, int jogadorId) {
		Partida partida = obterPartida(partidaId);
		Jogador quemSaiu = jogadorDaPartida(partida, jogadorId);
		if (partida.isFinalizada()) {
			return; //já tinha terminado: nada a fazer
		}
		encerrarPorDesconexao(partida, quemSaiu);
	}

	/**
	 * Resposta errada ou tempo esgotado: registra o erro no histórico, passa a vez ao
	 * adversário e monta a rodada dele. Se ele não tiver 3 perguntas inéditas, perde.
	 * Usado tanto por responder() quanto por expirar(), para as duas situações
	 * seguirem exatamente a mesma regra.
	 */
	private ResultadoRespostaDTO processarErro(Partida partida, Jogador jogador, String motivo) {
		historicoDAO.registrarResposta(partida.getIdPartida(), jogador.getId(),
				perguntaAtual(partida).getIdPergunta(), false);

		Jogador adversario = adversario(partida, jogador);
		partida.setJogadorDaVez(adversario); //muda para o adversario
		boolean rodadaFormada = iniciarRodada(partida);

		if (rodadaFormada) {
			prepararPergunta(partida);
			return new ResultadoRespostaDTO(false, true, false, false,
					motivo + ". A vez passa para " + adversario.getNome() + ".");
		}

		//se o adversário não tinha 3 perguntas inéditas- perde, e quem errou vence
		return new ResultadoRespostaDTO(false, true, false, true,
				motivo + ", mas " + adversario.getNome()
						+ " não tem 3 perguntas inéditas na categoria sorteada e perdeu. Você venceu!");
	}


	/**
	 * A pergunta acabou de ser definida, mas o relógio de 15 segundos só começa quando o
	 * jogador da vez a abrir (getPerguntaAtual). Enquanto isso, um alarme de segurança
	 * evita que a partida trave se esse jogador nunca buscar a pergunta.
	 */
	private void prepararPergunta(Partida partida) {
		int id = partida.getIdPartida();
		cancelarRelogio(id); //também apaga o prazo: sem prazo = pergunta ainda não aberta

		int jogadorId = partida.getJogadorDaVez().getId();
		int perguntaId = perguntaAtual(partida).getIdPergunta();

		ScheduledFuture<?> alarme = relogio.schedule(
				() -> expirar(id, jogadorId, perguntaId), SEGUNDOS_MAXIMO_PARA_ABRIR, TimeUnit.SECONDS);
		alarmes.put(id, alarme);
	}


	/**
	 * Liga o relógio da pergunta atual: guarda o horário limite e agenda um alarme
	 * para disparar expirar() quando o tempo acabar. Chamado quando o jogador da vez
	 * abre a pergunta pela primeira vez (substitui o alarme de segurança).
	 */
	private void iniciarRelogio(Partida partida) {
		int id = partida.getIdPartida();
		cancelarRelogio(id); //desarma o alarme anterior, se houver

		int jogadorId = partida.getJogadorDaVez().getId();
		int perguntaId = perguntaAtual(partida).getIdPergunta();

		prazos.put(id, System.currentTimeMillis() + SEGUNDOS_POR_PERGUNTA * 1000L);

		long atrasoMs = SEGUNDOS_POR_PERGUNTA * 1000L + FOLGA_MS;
		ScheduledFuture<?> alarme = relogio.schedule(
				() -> expirar(id, jogadorId, perguntaId), atrasoMs, TimeUnit.MILLISECONDS);
		alarmes.put(id, alarme);
	}

	//Desarma o alarme e apaga o prazo da partida
	private void cancelarRelogio(int partidaId) {
		ScheduledFuture<?> alarme = alarmes.remove(partidaId);
		if (alarme != null) {
			alarme.cancel(false);
		}
		prazos.remove(partidaId);
	}

	/**
	 * Executado pela thread do relógio quando o alarme toca. Só age se o jogador
	 * ainda estiver na mesma pergunta: se ele respondeu a tempo, nada acontece.
	 */
	private synchronized void expirar(int partidaId, int jogadorId, int perguntaId) {
		try {
			Partida p = partidas.get(partidaId);
			if (p == null || p.isFinalizada()) {
				return;
			}
			if (p.getJogadorDaVez().getId() != jogadorId
					|| perguntaAtual(p).getIdPergunta() != perguntaId) {
				return; //a resposta chegou a tempo e o jogo já seguiu
			}
			//Alarme de segurança que tocou no mesmo instante em que o jogador abriu a pergunta:
			//o relógio real já começou e o prazo ainda não venceu, então não pune
			Long prazo = prazos.get(partidaId);
			if (prazo != null && System.currentTimeMillis() < prazo) {
				return;
			}
			processarErro(p, p.getJogadorDaVez(), "Tempo esgotado");
			notificarMudanca(partidaId); //avisa os clientes por callback (se a partida acabou, o aviso de fim já saiu)
		} catch (Exception e) {
			// Erros dentro de tarefa agendada são engolidos em silêncio se não forem tratados
			e.printStackTrace();
		}
	}

	//Segundos que faltam para o fim da pergunta atual (calculado pelo servidor)
	private int segundosRestantes(int partidaId) {
		Long prazo = prazos.get(partidaId);
		if (prazo == null) {
			return 0;
		}
		long restanteMs = prazo - System.currentTimeMillis();
		return (int) Math.max(0, Math.ceil(restanteMs / 1000.0));
	}

	//Avisa que o estado mudou e a partida segue. Se ela acabou, o aviso de fim já saiu em finalizar().
	private void notificarMudanca(int partidaId) {
		PartidaListener o = ouvinte;
		Partida p = partidas.get(partidaId);
		if (o == null || p == null || p.isFinalizada()) {
			return;
		}
		try {
			o.placarAtualizado(partidaId);
		} catch (RuntimeException e) {
			e.printStackTrace(); //um erro ao avisar não pode desfazer a jogada
		}
	}

	private void avisarFim(int partidaId) {
		PartidaListener o = ouvinte;
		if (o == null) {
			return;
		}
		try {
			o.partidaEncerrada(partidaId);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	//Guarda o horário do último sinal do jogador; ignora partida encerrada ou jogador desconhecido
	private void marcarSinal(int partidaId, int jogadorId) {
		Map<Integer, Long> sinais = ultimosSinais.get(partidaId);
		if (sinais != null && sinais.containsKey(jogadorId)) {
			sinais.put(jogadorId, System.nanoTime());
		}
	}

	/**
	 * Executado pela thread do relógio a cada INTERVALO_VIGIA_SEGUNDOS. Procura jogadores sem sinal
	 * há mais de SEGUNDOS_SEM_SINAL_PARA_DESCONECTAR e encerra a partida deles.
	 */
	private synchronized void verificarConexoes() {
		try {
			long agora = System.nanoTime();
			// cópia das chaves: encerrar a partida remove a entrada do mapa durante o laço
			for (int partidaId : new ArrayList<>(ultimosSinais.keySet())) {
				Partida partida = partidas.get(partidaId);
				Map<Integer, Long> sinais = ultimosSinais.get(partidaId);
				if (partida == null || sinais == null || partida.isFinalizada()) {
					continue;
				}
				// Se os dois sumiram juntos, perde o jogador 1 (a ordem é só um desempate)
				for (Jogador jogador : List.of(partida.getJogador1(), partida.getJogador2())) {
					long semSinalMs = (agora - sinais.get(jogador.getId())) / 1_000_000;
					if (semSinalMs > SEGUNDOS_SEM_SINAL_PARA_DESCONECTAR * 1000L) {
						encerrarPorDesconexao(partida, jogador);
						break;
					}
				}
			}
		} catch (Exception e) {
			// Erros dentro de tarefa agendada são engolidos em silêncio se não forem tratados;
			// e uma exceção aqui cancelaria as próximas execuções do vigia
			e.printStackTrace();
		}
	}

	//Termina a partida porque um jogador saiu ou parou de responder: o adversário vence
	private void encerrarPorDesconexao(Partida partida, Jogador quemSaiu) {
		Jogador vencedor = adversario(partida, quemSaiu);
		motivosFim.put(partida.getIdPartida(),
				quemSaiu.getNome() + " desconectou. " + vencedor.getNome() + " venceu a partida.");
		finalizar(partida, vencedor);
	}

	//Devolve o jogador da partida com esse id (1 ou 2); erro se o id não pertence à partida
	private Jogador jogadorDaPartida(Partida partida, int jogadorId) {
		if (partida.getJogador1().getId() == jogadorId) {
			return partida.getJogador1();
		}
		if (partida.getJogador2().getId() == jogadorId) {
			return partida.getJogador2();
		}
		throw new IllegalArgumentException("Jogador " + jogadorId + " não pertence à partida.");
	}


	/**
	 * Método que inicia rodada: sorteia a categoria e monta a rodada com 3 perguntas ineditas
	 * Se não houver 3 perguntas inéditas, o jogador da vez perde e o outro ganha
	 */

	private boolean iniciarRodada(Partida partida) {
		Jogador jogador = partida.getJogadorDaVez();

		//Lista categorias que ainda estao disponiveis
		List<Categoria> disponiveis = categorias.stream()
				.filter(c -> !jaConquistou(jogador, c))
				.collect(Collectors.toList());
		if (disponiveis.isEmpty()) {
			throw new IllegalStateException("Jogador sem categorias restantes, mas a partida não terminou.");
		}

		//Sorteia uma categoria disponivel
		Categoria sorteada = disponiveis.get(random.nextInt(disponiveis.size()));
		List<Pergunta> perguntas = perguntaDAO.buscarIneditasPorCategoria(sorteada.getIdCategoria(), partida.getIdPartida(), jogador.getId(), PERGUNTAS_POR_RODADA);

		partida.setCategoriaRodadaAtual(sorteada);
		partida.setPerguntasRespondidasNaRodada(0);

		//Verifica se tem mais perguntas disponíveis do que respondidas
		if (perguntas.size() < PERGUNTAS_POR_RODADA) {
			finalizar(partida, adversario(partida, jogador));
			return false; //retorna false se terminar por derrota
		}

		perguntasDaRodada.put(partida.getIdPartida(), perguntas);
		return true; //retorna true se a rodada foi formada
	}


	//Método que finaliza a partida
	private void finalizar(Partida partida, Jogador vencedor) {
		partida.setFinalizada(true);
		vencedores.put(partida.getIdPartida(), vencedor);
		perguntasDaRodada.remove(partida.getIdPartida());
		cancelarRelogio(partida.getIdPartida()); //partida acabou: desarma o alarme
		ultimosSinais.remove(partida.getIdPartida()); //e para de vigiar a conexão dos jogadores
		avisarFim(partida.getIdPartida());
	}

	//Método que pega as 3 perguntas dessa partida
	private Pergunta perguntaAtual(Partida partida) {
		List<Pergunta> rodada = perguntasDaRodada.get(partida.getIdPartida());
		return rodada.get(partida.getPerguntasRespondidasNaRodada());
	}

	private Partida obterPartida(int partidaId) {
		Partida p = partidas.get(partidaId);
		if (p == null) {
			throw new IllegalArgumentException("Partida não encontrada: " + partidaId);
		}
		return p;
	}

	//método que muda para o outro jogador - se for jogador1, muda para jogador 2, vice-versa
	private Jogador adversario(Partida partida, Jogador jogador) {
		return partida.getJogador1().getId() == jogador.getId() ? partida.getJogador2() : partida.getJogador1();
	}

	// Compara pelo id da categoria.
	private boolean jaConquistou(Jogador jogador, Categoria categoria) {
		return jogador.getEstrelasConquistadas().stream()
				.anyMatch(c -> c.getIdCategoria() == categoria.getIdCategoria());
	}

	private List<String> nomesDasEstrelas(Jogador jogador) {
		return jogador.getEstrelasConquistadas().stream()
				.map(Categoria::getNome)
				.collect(Collectors.toList());
	}

}