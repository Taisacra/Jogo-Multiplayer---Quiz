package dao;

import model.Categoria;
import util.ConexaoH2;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public List<Categoria> buscarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nome FROM categoria";
        try (Connection conn = ConexaoH2.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");

                Categoria categoria = new Categoria(id, nome);

                categorias.add(categoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }
}