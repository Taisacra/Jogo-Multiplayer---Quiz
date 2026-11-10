package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
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
	
	private final Random random = new Random();
	
	private int proximoIdPartida = 1;
	
	
	//Método que cria a partida
	public synchronized Partida criarPartida(String nomeJogador1, String nomeJogador2) {
		List<Categoria> todas = categoriaDAO.buscarTodas();
		if(todas.isEmpty()) {
            throw new IllegalStateException("Nenhuma categoria cadastrada no banco de dados.");
        }
		
		categorias = todas;
		
		Jogador j1 = new Jogador(1, nomeJogador1);
		Jogador j2 = new Jogador(2, nomeJogador2);
		
		//Sorteio para ver quem começa
		Jogador primeiro = random.nextBoolean() ? j1 : j2;
		
		Partida partida = new Partida(proximoIdPartida++, j1, j2, primeiro, null, 0, false);
		partidas.put(partida.getIdPartida(), partida);
		iniciarRodada(partida);
		
		return partida;
		
	}
	
	
	//Método que controla cada jogada
	public synchronized ResultadoRespostaDTO responder(int partidaId, int jogadorId, int perguntaId, char alternativa) {
		
		Partida partida = obterPartida(partidaId);
		if(partida.isFinalizada()) {
			throw new IllegalStateException("A partida já foi encerrada.");
		}
		
		Jogador jogador = partida.getJogadorDaVez();
		if(jogador.getId() != jogadorId) {
			 throw new IllegalStateException("Não é a vez deste jogador.");
		}
		
		Pergunta atual = perguntaAtual(partida);
		if(atual.getIdPergunta() != perguntaId) {
			throw new IllegalArgumentException("Esta não é a pergunta atual da rodada.");
		}
		
		boolean acertou = Character.toUpperCase(alternativa) == 
				Character.toUpperCase(atual.getRespostaCorreta());
		
		 historicoDAO.registrarResposta(partida.getIdPartida(), jogador.getId(),
				 atual.getIdPergunta(), acertou);
		 
		 //se errar
		 if (!acertou) {
	            Jogador adversario = adversario(partida, jogador);
	            partida.setJogadorDaVez(adversario);
	            boolean rodadaFormada = iniciarRodada(partida);
	            
	            if (rodadaFormada) {
	                return new ResultadoRespostaDTO(false, true, false, false,
	                        "Resposta errada. A vez passa para " + adversario.getNome() + ".");
	            } 
	            
	         //se o adversário não tinha 3 perguntas inéditas: perde, e quem errou vence
	            return new ResultadoRespostaDTO(false, true, false, true,
	                    "Resposta errada, mas " + adversario.getNome()
	                            + " não tem 3 perguntas inéditas na categoria sorteada e perdeu. Você venceu!");
	        }
		 
		 //se acertar
	        int respondidas = partida.getPerguntasRespondidasNaRodada() + 1;
	        partida.setPerguntasRespondidasNaRodada(respondidas);
	 
	        if (respondidas < PERGUNTAS_POR_RODADA) {
	            return new ResultadoRespostaDTO(true, false, false, false, "Resposta correta!");
	        }
	 
	        // ---- acertou as 3: conquista a estrela da categoria
	        Categoria conquistada = partida.getCategoriaRodadaAtual();
	        if (!jaConquistou(jogador, conquistada)) {
	            jogador.adicionarEstrela(conquistada);
	        }
	 
	        if (jogador.getEstrelasConquistadas().size() >= categorias.size()) {
	            finalizar(partida, jogador);
	            return new ResultadoRespostaDTO(true, true, true, true,
	                    "Você conquistou a estrela de " + conquistada.getNome() + " e venceu a partida!");
	        }
	 
	        // continua jogando, em uma nova categoria
	        boolean rodadaFormada = iniciarRodada(partida);
	        if (rodadaFormada) {
	            return new ResultadoRespostaDTO(true, true, true, false,
	                    "Estrela de " + conquistada.getNome() + " conquistada! Nova rodada: "
	                            + partida.getCategoriaRodadaAtual().getNome() + ".");
	        }
	        return new ResultadoRespostaDTO(true, true, true, false,
	                "Estrela de " + conquistada.getNome() + " conquistada, mas não há 3 perguntas inéditas na próxima "
	                        + "categoria sorteada. Você perdeu a partida.");
	}
	 
//consultas
	
	 
    /** Pergunta que o jogador da vez deve responder agora (sem o gabarito). */
    public synchronized PerguntaDTO getPerguntaAtual(int partidaId) {
        Partida partida = obterPartida(partidaId);
        if (partida.isFinalizada()) {
            throw new IllegalStateException("A partida já foi encerrada.");
        }
        Pergunta p = perguntaAtual(partida);
        return new PerguntaDTO(p.getIdPergunta(), partida.getCategoriaRodadaAtual().getNome(), p.getEnunciado(),
                List.of(p.getAltA(), p.getAltB(), p.getAltC(), p.getAltD()));
    }
 
    public synchronized PlacarDTO getPlacar(int partidaId) {
        Partida p = obterPartida(partidaId);
        return new PlacarDTO(
                p.getJogador1().getNome(), nomesDasEstrelas(p.getJogador1()),
                p.getJogador2().getNome(), nomesDasEstrelas(p.getJogador2()),
                p.getJogadorDaVez().getNome());
    }
 
    public synchronized Partida getPartida(int partidaId) {
        return obterPartida(partidaId);
    }
 
    public synchronized boolean isFinalizada(int partidaId) {
        return obterPartida(partidaId).isFinalizada();
    }
 
    /** Vencedor da partida, ou null se ela ainda não terminou. */
    public synchronized Jogador getVencedor(int partidaId) {
        return vencedores.get(partidaId);
    }
 
	
	
	 /**
	  * Método que inicia rodada: sorteia a categoria e monta a rodada com 3 perguntas ineditas.
	  * Se não houver 3 perguntas inéditas, o jogador da vez perde e o outro ganha.
	  */
	
	private boolean iniciarRodada (Partida partida) {
		Jogador jogador = partida.getJogadorDaVez();
		
		List<Categoria> disponiveis = categorias.stream()
                .filter(c -> !jaConquistou(jogador, c))
                .collect(Collectors.toList());
		if(disponiveis.isEmpty()) {
			throw new IllegalStateException("Jogador sem categorias restantes, mas a partida não terminou.");
		}
		
		Categoria sorteada = disponiveis.get(random.nextInt(disponiveis.size()));
		List<Pergunta> perguntas = perguntaDAO.buscarIneditasPorCategoria(sorteada.getIdCategoria(), partida.getIdPartida(), jogador.getId(), PERGUNTAS_POR_RODADA);
		
		partida.setCategoriaRodadaAtual(sorteada);
		partida.setPerguntasRespondidasNaRodada(0);
		
		if (perguntas.size() < PERGUNTAS_POR_RODADA) {
            finalizar(partida, adversario(partida, jogador));
            return false; //retorna false se terminar por derrota
        }
		
		perguntasDaRodada.put(partida.getIdPartida(), perguntas);
		return true; //retorna true se a rodada foi formada
	}
	
    private void finalizar(Partida partida, Jogador vencedor) {
        partida.setFinalizada(true);
        vencedores.put(partida.getIdPartida(), vencedor);
        perguntasDaRodada.remove(partida.getIdPartida());
    }
    
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
