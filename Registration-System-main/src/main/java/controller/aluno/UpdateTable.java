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

        if (!sc.hasNextLine()) {
            System.out.println("No option provided. Aborting update.");
            return;
        }
        String optionStr = sc.nextLine();
        int opcao;
        try {
            opcao = Integer.parseInt(optionStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid option format. Try again.!");
            return;
        }


        String campo = "";
        String novoValor = "";

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
                if (!sc.hasNextLine()) {
                    System.out.println("No course ID provided. Aborting update.");
                    return;
                }
                int novoCursoId;
                try {
                    novoCursoId = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid course ID format. Try again.!");
                    return;
                }

                Courses cursos = Courses.getById(novoCursoId);

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
                System.out.println("Field updated successfully!\n");
            } else {
                System.out.println("No student was found with that enrollment number.\n");
            }

        } catch (SQLException e) {
            System.out.println("Error updating field");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Error processing input: " + e.getMessage());
            e.printStackTrace();
        }
    }
}