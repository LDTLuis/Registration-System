package model;

import controller.aluno.DeleteTable;
import controller.aluno.UpdateTable;
import dao.StudentDAO;
import text.SelectInterface;

import java.sql.Date;
import java.util.Scanner;

public class AdminUser extends Users {

    Scanner sc = new Scanner(System.in);
    StudentDAO alunoDAO = new StudentDAO();
    UpdateTable upd = new UpdateTable();
    DeleteTable del = new DeleteTable();
    SelectInterface si = new SelectInterface();

    public AdminUser(String login, String senha) {
        super(login, senha);
    }

    @Override
    public void showMenu() {

        StudentData pedro = new StudentData("Pedro",null ,"999-999" , Date.valueOf("2005-01-01"), Courses.getById(12), "123456789");
        StudentData luis = new StudentData("Luis", null ,"999-999" , Date.valueOf("2005-03-21"), Courses.getById(12), "999123981");

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
                    StudentData novoAluno = lerDadosAluno();
                    if (novoAluno != null) {
                        alunoDAO.insert(novoAluno);
                    }
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

    private StudentData lerDadosAluno() {
        try {
            System.out.print("Nome: ");
            String nome = sc.nextLine().trim();

            System.out.print("Telefone: ");
            String telefone = sc.nextLine().trim();

            System.out.print("Data de nascimento (yyyy-MM-dd): ");
            String dataStr = sc.nextLine().trim();
            java.sql.Date dataNascimento = java.sql.Date.valueOf(dataStr);

            System.out.print("Código do curso: ");
            int idCurso = Integer.parseInt(sc.nextLine().trim());
            Courses curso = Courses.getById(idCurso);
            if (curso == null) {
                System.out.println("Curso não encontrado.");
                return null;
            }

            System.out.print("CPF: ");
            String cpf = sc.nextLine().trim();

            return new StudentData(nome, telefone, telefone, dataNascimento, curso, cpf);

        } catch (Exception e) {
            System.out.println("Erro na leitura dos dados do aluno. Tente novamente.");
            return null;
        }
    }
}
