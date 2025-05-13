package controller.aluno;

import model.Courses;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateTable {
    public void updateTable() {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Update Student ===");
        System.out.print("Enter the student's enrollment number you want to update: ");
        String matricula = sc.nextLine();
        System.out.println();

        System.out.println("Which field would you like to update?");
        System.out.println("1 - Name");
        System.out.println("2 - Phone Number");
        System.out.println("3 - Date of Birth");
        System.out.println("4 - Course");
        System.out.println("5 - CPF");
        System.out.print("Enter the option number: ");
        int opcao = Integer.parseInt(sc.nextLine());

        String campo = "";
        String novoValor = "";
        int novoCurso = 0;

        switch (opcao) {
            case 1:
                campo = "nome";
                System.out.print("Enter the new name: ");
                novoValor = sc.nextLine().toUpperCase();
                break;
            case 2:
                campo = "telefone";
                System.out.print("Enter the new phone number: ");
                novoValor = sc.nextLine();
                break;
            case 3:
                campo = "data_nascimento";
                System.out.print("Enter the new date of birth (AAAA-MM-DD): ");
                novoValor = sc.nextLine();
                break;
            case 4:
                campo = "curso";
                System.out.print("Enter the new course id: ");
                novoCurso = Integer.parseInt(sc.nextLine());

                Courses cursos = Courses.getById(novoCurso);

                if(cursos == null){
                    System.out.println("Invalid course id. Try again.");
                    return;
                }
                novoValor = cursos.getNomeCurso();
                break;
            case 5:
                campo = "cpf";
                System.out.print("Enter the new CPF: ");
                novoValor = sc.nextLine();
                break;
            default:
                System.out.println("Invalid option. Try again.!");
                sc.close();
                return;
        }

        String sql = "UPDATE alunos SET " + campo + " = ? WHERE matricula = ?";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (campo.equals("data_nascimento")) {
                stmt.setDate(1, java.sql.Date.valueOf(novoValor));
            } else {
                stmt.setString(1, novoValor);
            }

            stmt.setString(2, matricula);

            int linhaAlterada = stmt.executeUpdate();

            if (linhaAlterada > 0) {
                System.out.println("\nField updated successfully!");
            } else {
                System.out.println("\nNo student was found with that enrollment number.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating field");
            e.printStackTrace();
        }
    }
}
