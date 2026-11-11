package dao;

import util.ConexaoH2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HistoricoRespostaDAO {

    public void registrarResposta(int partidaId, int jogadorId, int perguntaId, boolean acertou) {
        String sql = "INSERT INTO historico_resposta (partida_id, jogador_id, pergunta_id, acertou) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoH2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

        	stmt.setInt(1, partidaId);
            stmt.setInt(2, jogadorId);
            stmt.setInt(3, perguntaId);
            stmt.setBoolean(4, acertou);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //método para limpar o historico_resposta ao fim de uma partida
    public void limparTodo() {
        String sql = "DELETE FROM historico_resposta";
        try (Connection conn = ConexaoH2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
