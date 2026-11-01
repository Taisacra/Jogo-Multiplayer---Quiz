package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoH2 {
    // Utilizando banco de dados em arquivo local (pasta do projeto)
    private static final String URL = "jdbc:h2:./quizdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void inicializarBanco() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Tabela de Categorias
            stmt.execute("CREATE TABLE IF NOT EXISTS categoria (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL UNIQUE)");

            // Tabela de Perguntas
            stmt.execute("CREATE TABLE IF NOT EXISTS pergunta (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "categoria_id INT, " +
                    "enunciado TEXT NOT NULL, " +
                    "alternativa_a VARCHAR(255) NOT NULL, " +
                    "alternativa_b VARCHAR(255) NOT NULL, " +
                    "alternativa_c VARCHAR(255) NOT NULL, " +
                    "alternativa_d VARCHAR(255) NOT NULL, " +
                    "resposta_correta CHAR(1) NOT NULL, " +
                    "FOREIGN KEY (categoria_id) REFERENCES categoria(id))");

            // Histórico para garantir perguntas inéditas
            stmt.execute("CREATE TABLE IF NOT EXISTS historico_resposta (" +
                    "jogador_id INT, " +
                    "pergunta_id INT, " +
                    "acertou BOOLEAN, " +
                    "PRIMARY KEY (jogador_id, pergunta_id), " +
                    "FOREIGN KEY (pergunta_id) REFERENCES pergunta(id))");

            System.out.println("Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
