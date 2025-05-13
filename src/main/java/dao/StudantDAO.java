package dao;

import model.StudantData;
import util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;

public class StudantDAO {

    ConnectionFactory cf = new ConnectionFactory();

    public void createTable (){
        String sql = """
                CREATE TABLE IF NOT EXISTS alunos (
                    id SERIAL PRIMARY KEY,
                    nome VARCHAR(255) NOT NULL,
                    matricula VARCHAR(255) UNIQUE,
                    telefone VARCHAR(255),
                    data_nascimento DATE NOT NULL,
                    curso VARCHAR(255) NOT NULL,
                    cpf VARCHAR(255) NOT NULL
                );
                """;

        try(Connection conn = ConnectionFactory.getConnection();
            Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Student table created successfully!");

        } catch (SQLException e){
            System.out.println("Error creating table");
            e.printStackTrace();
        }
    }

    public void insert (StudantData aluno){
        String sql = "INSERT INTO alunos (nome, telefone, data_nascimento,curso, cpf) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome().toUpperCase());
            stmt.setString(2, aluno.getTelefone());
            stmt.setObject(3, aluno.getDataNascimento());
            stmt.setString(4, aluno.getCurso().getNomeCurso());
            stmt.setString(5, aluno.getCPF());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                aluno.setId(rs.getInt("id"));

                int idGerado = rs.getInt("id");

                int anoAtual = LocalDate.now().getYear();
                String codCurso = String.format("%02d", aluno.getCurso().getId());
                String codId = String.format("%04d", idGerado);
                String matricula = anoAtual + codCurso + codId;

                String sqlUpdate = "UPDATE alunos SET matricula = ? WHERE id = ?";
                PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                stmtUpdate.setString(1, matricula);
                stmtUpdate.setInt(2, idGerado);
                stmtUpdate.executeUpdate();

                System.out.println("Student successfully registered! Enrollment number: " + matricula);
            }

        }
        catch (SQLException e) {
            System.out.println("Error registering student.");
            e.printStackTrace();
        }
    }

}
