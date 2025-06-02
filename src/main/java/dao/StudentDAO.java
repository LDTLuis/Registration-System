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
            System.out.println("Student table created successfully!");

        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela de alunos.");
            e.printStackTrace();
        }
    }

    public void insert(StudentData aluno) {
        String sqlInsertAluno = "INSERT INTO alunos (nome, telefone, data_nascimento, curso, cpf) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInsertAluno)) {

            conn.setAutoCommit(false);

            stmt.setString(1, aluno.getNome().toUpperCase());
            stmt.setString(2, aluno.getTelefone());
            stmt.setObject(3, aluno.getDataNascimento());
            stmt.setString(4, aluno.getCurso().getNomeCurso());
            stmt.setString(5, aluno.getCPF());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                aluno.setId(rs.getInt("id"));
                int idGerado = aluno.getId();

                String matricula = gerarMatricula(idGerado, aluno.getCurso().getId());
                aluno.setMatricula(matricula);

                String sqlUpdateMatricula = "UPDATE alunos SET matricula = ? WHERE id = ?";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateMatricula)) {
                    stmtUpdate.setString(1, matricula);
                    stmtUpdate.setInt(2, idGerado);
                    stmtUpdate.executeUpdate();
                }

                String sqlInsertUsuario = "INSERT INTO usuarios (login, senha, tipo, matricula) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlInsertUsuario)) {
                    stmtUsuario.setString(1, matricula);
                    stmtUsuario.setString(2, aluno.getCPF()); // senha padrão = CPF
                    stmtUsuario.setString(3, "aluno");
                    stmtUsuario.setString(4, matricula);
                    stmtUsuario.executeUpdate();
                }

                conn.commit();
                System.out.println("Aluno e usuário inseridos com sucesso. Matrícula: " + matricula);
                System.out.println();

            } else {
                conn.rollback();
                System.out.println("Erro ao obter ID do aluno.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao registrar aluno e criar usuário.");
            e.printStackTrace();
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
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
