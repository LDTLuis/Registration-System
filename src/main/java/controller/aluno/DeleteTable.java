package controller.aluno;

import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class DeleteTable {
    public void deleteTable() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Delete Student ===");
        System.out.print("Enter the student's enrollment number you want to delete: ");
        String matricula = sc.next();

        String sql = "DELETE FROM alunos WHERE matricula = ?";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricula);

            int linhaDeletada = stmt.executeUpdate();

            if (linhaDeletada > 0) {
                System.out.println("\nStudent successfully deleted!");
            } else {
                System.out.println("\nNo student was found with that enrollment number.");
            }

        } catch (Exception e) {
            System.out.println("Error deleting student.");
            e.printStackTrace();
        }
    }
}
