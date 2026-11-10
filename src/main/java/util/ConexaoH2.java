package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoH2 {
    // Banco em arquivo local (pasta do projeto)
    private static final String URL = "jdbc:h2:./quizdb;AUTO_SERVER=TRUE;MODE=MySQL";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void inicializarBanco() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            if (precisaPopular(st)) {
                executarScript(st, "/init.sql");
                System.out.println("Banco criado e populado com sucesso.");
            } else {
                System.out.println("Banco já populado, init.sql ignorado.");
            }
        } catch (SQLException | IOException e) {
            System.out.println("Erro ao inicializar o banco de dados:");
            e.printStackTrace();

        }
    }

    // True se a tabela categoria não existe ainda ou está vazia
    private static boolean precisaPopular(Statement st) {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM categoria")) {
            rs.next();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            return true; // tabela não existe
        }
    }

    // Lê o arquivo de src/main/resources e executa o script inteiro de uma vez
    private static void executarScript(Statement st, String recurso) throws IOException, SQLException {
        try (InputStream in = ConexaoH2.class.getResourceAsStream(recurso)) {
            if (in == null) throw new IOException("Arquivo não encontrado: " + recurso);
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            st.execute(sql);
        }
    }
}
