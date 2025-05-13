package dao;

import model.Admin;
import model.StudantUser;
import model.Users;
import util.ConnectionFactory;

import java.sql.*;

public class UserDAO {
    public Users fazerLogin(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String tipo = rs.getString("tipo");

                if (tipo.equalsIgnoreCase("ADMIN")) {
                    return new Admin(login, senha);
                } else if (tipo.equalsIgnoreCase("ALUNO")) {
                    String matricula = rs.getString("matricula");
                    return new StudantUser(login, senha, matricula);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
