package dao;

import util.ConexaoH2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HistoricoRespostaDAO {

    public void registrarResposta(int jogadorId, int perguntaId, boolean acertou) {
        String sql = "INSERT INTO historico_resposta (jogador_id, pergunta_id, acertou) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoH2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jogadorId);
            stmt.setInt(2, perguntaId);
            stmt.setBoolean(3, acertou);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
