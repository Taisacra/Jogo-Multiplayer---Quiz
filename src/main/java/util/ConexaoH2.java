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
        try (Connection conn = getConnection()) {
            System.out.println("Banco de dados inicializado e populado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar o banco de dados:");
            e.printStackTrace();
        }
    }
}
