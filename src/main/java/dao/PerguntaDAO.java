package dao;

import model.Pergunta;
import util.ConexaoH2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerguntaDAO {

    /**
     * Busca perguntas inéditas de uma categoria para formar a rodada do jogador.
     */
    public List<Pergunta> buscarIneditasPorCategoria(int categoriaId, int jogadorId, int limite) {
        List<Pergunta> perguntas = new ArrayList<>();

        // Retorna apenas perguntas que NÃO estão no histórico do jogador
        String sql = "SELECT * FROM pergunta p " +
                "WHERE p.categoria_id = ? AND p.id NOT IN (" +
                "  SELECT h.pergunta_id FROM historico_resposta h WHERE h.jogador_id = ?" +
                ") ORDER BY RAND() LIMIT ?";

        try (Connection conn = ConexaoH2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            stmt.setInt(2, jogadorId);
            stmt.setInt(3, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Extrair os dados da linha atual
                    int id = rs.getInt("id");
                    int idCategoria = rs.getInt("categoria_id");
                    String enunciado = rs.getString("enunciado");
                    String altA = rs.getString("alternativa_a");
                    String altB = rs.getString("alternativa_b");
                    String altC = rs.getString("alternativa_c");
                    String altD = rs.getString("alternativa_d");

                    // Como o H2 pode devolver a coluna CHAR como String, extraímos o primeiro caractere
                    char respostaCorreta = rs.getString("resposta_correta").charAt(0);

                    // 2. Instanciar o objeto Pergunta (exemplo a instanciar vazio e a usar setters)
                    Pergunta pergunta = new Pergunta(id,idCategoria, enunciado, altA, altB, altC, altD, respostaCorreta);
                    
                    // adicionar a lista e será devolvida pelo método buscarIneditasPorCategoria
                    perguntas.add(pergunta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return perguntas;
    }
}