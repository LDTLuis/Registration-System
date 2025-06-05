package model;

import controller.aluno.DeleteTable;
import controller.aluno.UpdateTable;
import dao.StudentDAO;
import dao.ClassDAO;
import text.SelectInterface;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class AdminUser extends Users {

    private Scanner sc;
    private StudentDAO alunoDAO;
    private ClassDAO turmaDAO;
    private UpdateTable updateTable;
    private DeleteTable deleteTable;
    private SelectInterface selectInterface;

    public AdminUser(String login, String senha) {
        super(login, senha);
        this.sc = new Scanner(System.in);
        this.alunoDAO = new StudentDAO();
        this.turmaDAO = new ClassDAO();
        this.updateTable = new UpdateTable();
        this.deleteTable = new DeleteTable();
        this.selectInterface = new SelectInterface();
    }

    public void setScanner(Scanner scanner) {
        this.sc = scanner;
    }

    public void setAlunoDAO(StudentDAO alunoDAO) {
        this.alunoDAO = alunoDAO;
    }

    public void setTurmaDAO(ClassDAO turmaDAO) {
        this.turmaDAO = turmaDAO;
    }

    public void setUpdateTable(UpdateTable updateTable) {
        this.updateTable = updateTable;
    }

    public void setDeleteTable(DeleteTable deleteTable) {
        this.deleteTable = deleteTable;
    }

    public void setSelectInterface(SelectInterface selectInterface) {
        this.selectInterface = selectInterface;
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
            System.out.println("=== Menu Admin ===");
            System.out.println("1 - Inserir Aluno");
            System.out.println("2 - Atualizar Aluno");
            System.out.println("3 - Listar Alunos");
            System.out.println("4 - Deletar Aluno");
            System.out.println("5 - Criar Turma");
            System.out.println("6 - Inserir Aluno em Turma (via CPF)");
            System.out.println("7 - Listar Turmas");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1":
                    StudentData novoAluno = lerDadosAluno();
                    if (novoAluno != null) {
                        alunoDAO.insert(novoAluno);
                    }
                    break;
                case "2":
                    updateTable.updateTable();
                    break;
                case "3":
                    selectInterface.showMenu();
                    break;
                case "4":
                    deleteTable.deleteTable();
                    break;
                case "5":
                    criarTurma();
                    break;
                case "6":
                    inserirAlunoEmTurmaPorCpf();
                    break;
                case "7":
                    menuListarTurmas();
                    break;
                case "0":
                    System.out.println("Saindo...");
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
            if (running) {
                System.out.println();
            }
        }
    }

    StudentData lerDadosAluno() {
        try {
            System.out.print("Nome: ");
            String nome = sc.nextLine().trim();

            System.out.print("Telefone: ");
            String telefone = sc.nextLine().trim();

            System.out.print("Data de nascimento (yyyy-MM-dd): ");
            String dataStr = sc.nextLine().trim();
            Date dataNascimento = Date.valueOf(dataStr);

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

    void criarTurma() {
        try {
            System.out.print("Nome da Turma: ");
            String nomeTurma = sc.nextLine().trim();

            System.out.print("Código do Curso: ");
            int idCurso = Integer.parseInt(sc.nextLine().trim());
            Courses curso = Courses.getById(idCurso);
            if (curso == null) {
                System.out.println("Curso não encontrado.");
                return;
            }

            Class turma = new Class();
            turma.setNome(nomeTurma);
            turma.setCurso(curso);

            turmaDAO.inserirTurma(turma);
            System.out.println("Turma criada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao criar turma.");
        }
    }

    void inserirAlunoEmTurmaPorCpf() {
        try {
            System.out.print("CPF do aluno: ");
            String cpf = sc.nextLine().trim().replaceAll("[^\\d]", "");

            System.out.print("ID da Turma: ");
            int idTurma = Integer.parseInt(sc.nextLine().trim());

            Integer alunoId = alunoDAO.getIdByCpf(cpf);

            if (alunoId == null) {
                System.out.println("Aluno com CPF " + cpf + " não encontrado.");
                return;
            }

            turmaDAO.inserirAlunoNaTurma(alunoId, idTurma);
            System.out.println("Aluno inserido na turma com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao inserir aluno na turma: " + e.getMessage());
        }
    }

    private void menuListarTurmas() {
        boolean running = true;
        while (running) {
            System.out.println("=== Listar Turmas - Menu ===");
            System.out.println("1 - Listar todas as turmas");
            System.out.println("2 - Listar turmas por curso");
            System.out.println("3 - Voltar");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1":
                    List<Class> todasTurmas = turmaDAO.listarTodasTurmas();
                    if (todasTurmas.isEmpty()) {
                        System.out.println("Nenhuma turma cadastrada.");
                    } else {
                        System.out.println("Todas as turmas:");
                        for (Class t : todasTurmas) {
                            System.out.println(t);
                        }
                    }
                    break;
                case "2":
                    System.out.print("Digite o nome do curso: ");
                    String nomeCurso = sc.nextLine().trim();
                    Courses curso = Courses.getByNomeCurso(nomeCurso);
                    if (curso == null) {
                        System.out.println("Curso inválido.");
                        break;
                    }
                    List<Class> turmasCurso = turmaDAO.listarTurmasPorCurso(curso);
                    if (turmasCurso.isEmpty()) {
                        System.out.println("Nenhuma turma para o curso " + curso.getNomeCurso());
                    } else {
                        System.out.println("Turmas do curso " + curso.getNomeCurso() + ":");
                        for (Class t : turmasCurso) {
                            System.out.println(t);
                        }
                    }
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
            if (running) {
                System.out.println();
            }
        }
    }
}