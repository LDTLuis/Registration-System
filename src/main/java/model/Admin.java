package model;

import controller.aluno.DeleteTable;
import controller.aluno.UpdateTable;
import dao.StudantDAO;
import text.SelectInterface;

import java.sql.Date;
import java.util.Scanner;

public class Admin extends Users {
    public Admin(String login, String senha) {
        super(login, senha);
    }

    @Override
    public void showMenu() {

        Scanner sc = new Scanner(System.in);
        StudantDAO alunoDAO = new StudantDAO();
        UpdateTable upd = new UpdateTable();
        DeleteTable del = new DeleteTable();
        SelectInterface si = new SelectInterface();

        StudantData pedro = new StudantData("Pedro",null,"999-999" , Date.valueOf("2005-01-01"), Courses.getById(12), "123456789");
        StudantData luis = new StudantData("Luis",null,"999-999" , Date.valueOf("2005-03-21"), Courses.getById(12), "999123981");

        while (true) {
            System.out.println("=== Menu Admin ===");
            System.out.println("1 - Insert Student");
            System.out.println("2 - Update Student");
            System.out.println("3 - List Student");
            System.out.println("4 - Delete Student");
            System.out.println("==================");
            System.out.print("Enter the desired option or 'Exit' to finish: ");
            String opcao = sc.nextLine();
            System.out.println();

            if (opcao.equals("Exit")) {
                break;
            }

            switch (opcao) {
                case "1":
                    alunoDAO.createTable();
                    alunoDAO.insert(pedro);
                    alunoDAO.insert(luis);
                    System.out.println();
                    break;
                case "2":
                    upd.updateTable();
                    break;
                case "3":
                    si.showMenu();
                    break;
                case "4":
                    del.deleteTable();
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }
}
