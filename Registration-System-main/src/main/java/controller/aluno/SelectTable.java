package controller.aluno;


import model.StudentData;
import model.Courses;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectTable {

    public List<StudentData> buscarAluno(String nomeBuscado) {
        List<StudentData> alunos = new ArrayList<>();
        String sql = "SELECT * FROM alunos WHERE nome LIKE ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nomeBuscado + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentData a = new StudentData(
                            rs.getString("nome"),
                            rs.getString("matricula"),
                            rs.getString("telefone"),
                            rs.getDate("data_nascimento"),
                            Courses.valueOf(rs.getString("curso").toUpperCase().replace(" ", "_")),
                            rs.getString("cpf")
                    );
                    alunos.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing course enum from database value: " + e.getMessage());
        }
        return alunos;
    }

    public List<StudentData> buscarAlunoMatricula(String matriculaBuscada) {
        List<StudentData> alunos = new ArrayList<>();
        String sql = "SELECT * FROM alunos WHERE matricula LIKE ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + matriculaBuscada + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    StudentData a = new StudentData(
                            rs.getString("nome"),
                            rs.getString("matricula"),
                            rs.getString("telefone"),
                            rs.getDate("data_nascimento"),
                            Courses.getByNomeCurso(rs.getString("curso")),
                            rs.getString("cpf")
                    );
                    alunos.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }

    public StudentData buscarPorMatricula(String matricula) {
        String sql = "SELECT * FROM alunos WHERE matricula = ?";
        StudentData aluno = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricula);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    aluno = new StudentData(
                            rs.getString("nome"),
                            rs.getString("matricula"),
                            rs.getString("telefone"),
                            rs.getDate("data_nascimento"),
                            Courses.getByNomeCursos(rs.getString("curso")),
                            rs.getString("cpf")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluno;
    }
}