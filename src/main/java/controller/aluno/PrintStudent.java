package controller.aluno;

import model.StudantData;
import util.ConnectionFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PrintStudent {

    public void printTable(String sql) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            JFrame frame = new JFrame("Student List");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            DefaultTableModel model = new DefaultTableModel(new String[]{
                    "ID", "Name", "Enrollment", "Phone", "Date of Birth", "Course", "CPF"
            }, 0);

            while (rs.next()) {
                Object[] row = {
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("matricula"),
                        rs.getString("telefone"),
                        rs.getString("data_nascimento"),
                        rs.getString("curso"),
                        rs.getString("cpf")
                };
                model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            frame.add(scrollPane);
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error Reading table: \n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public static void imprimirAlunos(List<StudantData> alunos) {
        if (alunos.isEmpty()) {
            System.out.println("No Student found.");
            return;
        }

        for (StudantData a : alunos) {
            System.out.println("==== Student Details ====");
            System.out.println("Name: " + a.getNome());
            System.out.println("Enrollment: " + a.getMatricula());
            System.out.println("Phone: " + a.getTelefone());
            System.out.println("Date of Birth: " + a.getDataNascimento());
            System.out.println("Course: " + a.getCurso().getNomeCurso().toUpperCase());
            System.out.println("CPF: " + a.getCPF());
            System.out.println("======================");
            System.out.println();
        }
    }

    public static void imprimirAluno(StudantData a) {
        if (a == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("==== Student Details ====");
        System.out.println("Name: " + a.getNome());
        System.out.println("Enrollment: " + a.getMatricula());
        System.out.println("Phone: " + a.getTelefone());
        System.out.println("Date of Birth: " + a.getDataNascimento());
        System.out.println("Course: " + a.getCurso().getNomeCurso().toUpperCase());
        System.out.println("CPF: " + a.getCPF());
        System.out.println("====================");
        System.out.println();
    }

    public void readTable() {
        PrintStudent ps = new PrintStudent();
        String sql = "SELECT * FROM alunos";
        ps.printTable(sql);
    }
}
