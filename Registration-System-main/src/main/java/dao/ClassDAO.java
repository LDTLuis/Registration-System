package dao;

import model.Courses;
import model.Class;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    public void criarTabelasTurmaEAlunoTurma() {
        String sqlTurma = """
                CREATE TABLE IF NOT EXISTS turmas (
                    id SERIAL PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    curso VARCHAR(100) NOT NULL
                );
                """;

        String sqlAlunoTurma = """
                CREATE TABLE IF NOT EXISTS alunos_turmas (
                    aluno_id INT REFERENCES alunos(id),
                    turma_id INT REFERENCES turmas(id),
                    PRIMARY KEY (aluno_id, turma_id)
                );
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlTurma);
            stmt.execute(sqlAlunoTurma);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inserirTurma(Class turma) {
        criarTabelasTurmaEAlunoTurma();
        String sql = "INSERT INTO turmas (nome, curso) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, turma.getNome());
            stmt.setString(2, turma.getCurso().getNomeCurso());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inserirAlunoNaTurma(int alunoId, int turmaId) {
        String sql = "INSERT INTO alunos_turmas (aluno_id, turma_id) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            stmt.setInt(2, turmaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Class> listarTodasTurmas() {
        List<Class> turmas = new ArrayList<>();
        String sql = "SELECT * FROM turmas";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                turmas.add(new Class(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        Courses.getByNomeCurso(rs.getString("curso"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turmas;
    }

    public List<Class> listarTurmasPorCurso(Courses curso) {
        List<Class> turmas = new ArrayList<>();
        String sql = "SELECT * FROM turmas WHERE curso = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getNomeCurso());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    turmas.add(new Class(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            curso
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turmas;
    }

    public List<Class> listarTurmasDoAluno(int alunoId) {
        List<Class> turmas = new ArrayList<>();
        String sql = """
        SELECT t.id, t.nome, t.curso
        FROM turmas t
        JOIN alunos_turmas at ON t.id = at.turma_id
        WHERE at.aluno_id = ?
    """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    turmas.add(new Class(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            Courses.getByNomeCurso(rs.getString("curso"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turmas;
    }
}