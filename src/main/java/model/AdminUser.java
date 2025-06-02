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

    private final Scanner sc = new Scanner(System.in);
    private final StudentDAO alunoDAO = new StudentDAO();
    private final ClassDAO turmaDAO = new ClassDAO();
    private final UpdateTable upd = new UpdateTable();
    private final DeleteTable del = new DeleteTable();
    private final SelectInterface si = new SelectInterface();

    public AdminUser(String login, String senha) {
        super(login, senha);
    }

    @Override
    public void showMenu() {
        while (true) {
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
                case "1" -> {
                    StudentData novoAluno = lerDadosAluno();
                    if (novoAluno != null) {
                        alunoDAO.insert(novoAluno);
                    }
                }
                case "2" -> upd.updateTable();
                case "3" -> si.showMenu();
                case "4" -> del.deleteTable();
                case "5" -> criarTurma();
                case "6" -> inserirAlunoEmTurmaPorCpf();
                case "7" -> menuListarTurmas();
                case "0" -> {
                    System.out.println("Saindo...");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
            System.out.println();
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

    private void criarTurma() {
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

    private void inserirAlunoEmTurmaPorCpf() {
        try {
            System.out.print("CPF do aluno: ");
            String cpf = sc.nextLine().trim().replaceAll("[^\\d]", ""); // Limpa pontuação

            System.out.print("ID da Turma: ");
            int idTurma = Integer.parseInt(sc.nextLine().trim());

            StudentDAO alunoDAO = new StudentDAO();
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
        while (true) {
            System.out.println("=== Listar Turmas - Menu ===");
            System.out.println("1 - Listar todas as turmas");
            System.out.println("2 - Listar turmas por curso");
            System.out.println("3 - Voltar");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1" -> {
                    List<Class> todasTurmas = turmaDAO.listarTodasTurmas();
                    if (todasTurmas.isEmpty()) {
                        System.out.println("Nenhuma turma cadastrada.");
                    } else {
                        System.out.println("Todas as turmas:");
                        for (Class t : todasTurmas) {
                            System.out.println(t);
                        }
                    }
                }
                case "2" -> {
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
                }
                case "3" -> {
                    return; // sair do menu
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
            System.out.println();
        }
    }
}
