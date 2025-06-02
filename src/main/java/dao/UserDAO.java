package dao;

import model.AdminUser;
import model.StudentUser;
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
                    return new AdminUser(login, senha);
                } else if (tipo.equalsIgnoreCase("ALUNO")) {
                    String matricula = rs.getString("matricula");
                    return new StudentUser(login, senha, matricula);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void createTable() {

        ConnectionFactory cf = new ConnectionFactory();

        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    login VARCHAR(50) PRIMARY KEY,
                    senha VARCHAR(50) NOT NULL,
                    tipo VARCHAR(10) NOT NULL,
                    matricula VARCHAR(20),
                    FOREIGN KEY (matricula) REFERENCES alunos(matricula)
                );
                """;

        try(Connection conn = ConnectionFactory.getConnection();
            Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Users table created successfully!");

        } catch (SQLException e){
            System.out.println("Error creating table");
            e.printStackTrace();
        }
    }

    public void insertUser(String login, String password, String type, String registration) {
        String sql = "INSERT INTO usuarios (login, senha, tipo, matricula) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, type);
            stmt.setString(4, registration);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User inserted successfully!\n");
            } else {
                System.out.println("No user was inserted.\n");
            }

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
    }

}
