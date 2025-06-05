package dao;

import model.StudentData;
import util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;

public class StudentDAO {

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS alunos (
                    id SERIAL PRIMARY KEY,
                    nome VARCHAR(255) NOT NULL,
                    matricula VARCHAR(255) UNIQUE,
                    telefone VARCHAR(255),
                    data_nascimento DATE NOT NULL,
                    curso VARCHAR(255) NOT NULL,
                    cpf VARCHAR(255) NOT NULL UNIQUE
                );
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(StudentData aluno) {
        String sqlInsertAluno = "INSERT INTO alunos (nome, telefone, data_nascimento, curso, cpf) VALUES (?, ?, ?, ?, ?) RETURNING id";
        String sqlUpdateMatricula = "UPDATE alunos SET matricula = ? WHERE id = ?";
        String sqlInsertUsuario = "INSERT INTO usuarios (login, senha, tipo, matricula) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtInsertAluno = null;
        PreparedStatement stmtUpdateMatricula = null;
        PreparedStatement stmtInsertUsuario = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            stmtInsertAluno = conn.prepareStatement(sqlInsertAluno);
            stmtInsertAluno.setString(1, aluno.getNome().toUpperCase());
            stmtInsertAluno.setString(2, aluno.getTelefone());
            stmtInsertAluno.setObject(3, aluno.getDataNascimento());
            stmtInsertAluno.setString(4, aluno.getCurso().getNomeCurso());
            stmtInsertAluno.setString(5, aluno.getCPF());
            rs = stmtInsertAluno.executeQuery();

            if (rs.next()) {
                aluno.setId(rs.getInt("id"));
                int idGerado = aluno.getId();
                String matricula = gerarMatricula(idGerado, aluno.getCurso().getId());
                aluno.setMatricula(matricula);

                stmtUpdateMatricula = conn.prepareStatement(sqlUpdateMatricula);
                stmtUpdateMatricula.setString(1, matricula);
                stmtUpdateMatricula.setInt(2, idGerado);
                stmtUpdateMatricula.executeUpdate();

                stmtInsertUsuario = conn.prepareStatement(sqlInsertUsuario);
                stmtInsertUsuario.setString(1, matricula);
                stmtInsertUsuario.setString(2, aluno.getCPF());
                stmtInsertUsuario.setString(3, "aluno");
                stmtInsertUsuario.setString(4, matricula);
                stmtInsertUsuario.executeUpdate();

                conn.commit();
            } else {
                if (conn != null) {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmtInsertAluno != null) stmtInsertAluno.close();
                if (stmtUpdateMatricula != null) stmtUpdateMatricula.close();
                if (stmtInsertUsuario != null) stmtInsertUsuario.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String gerarMatricula(int id, int codCurso) {
        int ano = LocalDate.now().getYear();
        return ano + String.format("%02d", codCurso) + String.format("%04d", id);
    }

    public Integer getIdByCpf(String cpf) {
        String sql = "SELECT id FROM alunos WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}