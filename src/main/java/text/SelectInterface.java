package text;

import controller.aluno.PrintStudent;
import controller.aluno.SelectTable;
import model.StudantData;

import java.util.List;
import java.util.Scanner;

public class SelectInterface {

    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        SelectTable selectBy = new SelectTable();
        PrintStudent printStudent = new PrintStudent();

        System.out.println("=== Search Menu === ");
        System.out.println("1 - Select for name");
        System.out.println("2 - Select for matricula");
        System.out.println("3 - List All Students");
        System.out.println("======================");
        System.out.print("Enter the desired option or 'Exit' to finish: ");

        String opcao = "";
        System.out.println();

        while (true) {
            opcao = System.console().readLine();
            if (opcao.equals("Exit")) {
                break;
            }

            switch (opcao) {
                case "1":
                    System.out.println("Enter the name of the student you want to search for: ");
                    String nomeBuscado = sc.nextLine().toUpperCase();

                    selectBy.buscarAluno(nomeBuscado);
                    System.out.println(nomeBuscado);
                    List<StudantData> alunos = selectBy.buscarAluno(nomeBuscado);
                    PrintStudent.imprimirAlunos(alunos);

                    break;
                case "2":
                    System.out.println("Enter the student's enrollment number you want to search for: ");
                    String matriculaBuscada = sc.nextLine().toUpperCase();

                    selectBy.buscarAlunoMatricula(matriculaBuscada);
                    System.out.println(matriculaBuscada);
                    alunos = selectBy.buscarAlunoMatricula(matriculaBuscada);
                    PrintStudent.imprimirAlunos(alunos);

                    break;
                case "3":
                    printStudent.readTable();
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }
}
