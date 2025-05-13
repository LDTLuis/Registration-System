package model;

import controller.aluno.SelectTable;
import controller.aluno.PrintStudent;

import java.util.Scanner;

public class StudantUser extends Users {

    Scanner sc = new Scanner(System.in);
    PrintStudent printStudent = new PrintStudent();
    SelectTable selectTable = new SelectTable();

    private String matricula;

    public StudantUser(String login, String senha, String matricula) {
        super(login, senha);
        this.matricula = matricula;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("=== MENU STUDENT ===");
            System.out.println("1 - Show My Data");
            System.out.println("2 - Update My Data");
            System.out.println("====================");
            System.out.print("Enter the desired option or 'Exit' to finish: ");
            String opcao = sc.nextLine();

            if (opcao.equals("Exit")) {
                break;
            }

            switch (opcao) {
                case "1":
                    StudantData aluno = selectTable.buscarPorMatricula(matricula);
                    printStudent.imprimirAluno(aluno);
                    break;
            }
        }
    }

    public String getMatricula() {
        return matricula;
    }
}
