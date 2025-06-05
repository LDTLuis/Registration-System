package model;

import controller.aluno.SelectTable;
import controller.aluno.PrintStudent;
import dao.StudentDAO;
import dao.ClassDAO;

import java.util.List;
import java.util.Scanner;

public class StudentUser extends Users {

    private Scanner sc;
    private PrintStudent printStudent;
    private SelectTable selectTable;
    private ClassDAO turmaDAO;
    private StudentDAO alunoDAO;

    private final String matricula;

    public StudentUser(String login, String senha, String matricula) {
        super(login, senha);
        this.matricula = matricula;
        this.sc = new Scanner(System.in);
        this.printStudent = new PrintStudent();
        this.selectTable = new SelectTable();
        this.turmaDAO = new ClassDAO();
        this.alunoDAO = new StudentDAO();
    }

    public void setScanner(Scanner scanner) {
        this.sc = scanner;
    }

    public void setPrintStudent(PrintStudent printStudent) {
        this.printStudent = printStudent;
    }

    public void setSelectTable(SelectTable selectTable) {
        this.selectTable = selectTable;
    }

    public void setTurmaDAO(ClassDAO turmaDAO) {
        this.turmaDAO = turmaDAO;
    }

    public void setAlunoDAO(StudentDAO alunoDAO) {
        this.alunoDAO = alunoDAO;
    }

    public void closeScanner() {
        if (this.sc != null) {
            this.sc.close();
        }
    }

    @Override
    public void showMenu() {
        boolean running = true;
        while (running) {
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
                    running = false;
                    break;
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
        Integer alunoId = alunoDAO.getIdByCpf(this.matricula);

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