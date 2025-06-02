package model;

import controller.aluno.SelectTable;
import controller.aluno.PrintStudent;
import dao.StudentDAO;
import dao.ClassDAO;

import java.util.List;
import java.util.Scanner;

public class StudentUser extends Users {

    private final Scanner sc = new Scanner(System.in);
    private final PrintStudent printStudent = new PrintStudent();
    private final SelectTable selectTable = new SelectTable();
    private final ClassDAO turmaDAO = new ClassDAO();

    private final String matricula;

    public StudentUser(String login, String senha, String matricula) {
        super(login, senha);
        this.matricula = matricula;
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("=== MENU STUDENT ===");
            System.out.println("1 - Mostrar meus dados");
            System.out.println("2 - Listar minhas turmas");
            System.out.println("0 - Sair");
            System.out.println("====================");
            System.out.print("Digite a opção desejada: ");
            String opcao = sc.nextLine();
            System.out.println();

            switch (opcao) {
                case "0":
                    return;
                case "1":
                    StudentData aluno = selectTable.buscarPorMatricula(matricula);
                    if (aluno != null) {
                        printStudent.imprimirAluno(aluno);
                    } else {
                        System.out.println("Aluno não encontrado.");
                    }
                    break;
                case "2":
                    listarMinhasTurmas();
                    break;
                default:
                    System.out.println("Opção inválida.\n");
            }
        }
    }

    private void listarMinhasTurmas() {
        StudentDAO alunoDAO = new StudentDAO();
        Integer alunoId = alunoDAO.getIdByCpf(matricula); // matricula = CPF nesse caso

        if (alunoId == null) {
            System.out.println("Aluno não encontrado.");
            return;
        }

        List<Class> turmas = turmaDAO.listarTurmasDoAluno(alunoId);

        if (turmas.isEmpty()) {
            System.out.println("Você não está matriculado em nenhuma turma.");
        } else {
            System.out.println("=== Suas Turmas ===");
            for (Class turma : turmas) {
                System.out.println("ID: " + turma.getId());
                System.out.println("Nome: " + turma.getNome());
                System.out.println("Curso: " + turma.getCurso().getNomeCurso());
                System.out.println("---------------------------");
            }
        }
    }

    public String getMatricula() {
        return matricula;
    }
}
