package model;

import controller.aluno.PrintStudent;
import controller.aluno.SelectTable;
import dao.ClassDAO;
import dao.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentUserTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Mock
    private PrintStudent mockPrintStudent;
    @Mock
    private SelectTable mockSelectTable;
    @Mock
    private ClassDAO mockTurmaDAO;
    @Mock
    private StudentDAO mockAlunoDAO;

    private StudentUser studentUser;
    private final String login = "studentLogin";
    private final String senha = "studentPassword";
    private final String matricula = "M12345";

    @BeforeEach
    void setUp() {
        studentUser = new StudentUser(login, senha, matricula);
        studentUser.setPrintStudent(mockPrintStudent);
        studentUser.setSelectTable(mockSelectTable);
        studentUser.setTurmaDAO(mockTurmaDAO);
        studentUser.setAlunoDAO(mockAlunoDAO);

        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        studentUser.setScanner(new Scanner(System.in));
    }

    @AfterEach
    void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
        studentUser.closeScanner();
    }

    @Test
    void constructorShouldSetLoginSenhaMatricula() {
        assertEquals(login, studentUser.login);
        assertEquals(senha, studentUser.senha);
        assertEquals(matricula, studentUser.getMatricula());
    }

    @Test
    void getMatriculaShouldReturnMatricula() {
        assertEquals(matricula, studentUser.getMatricula());
    }

    @Test
    void showMenuOption1ShouldShowStudentData() {
        provideInput("1\n0\n");
        StudentData mockStudentData = new StudentData("Nome Teste", matricula, "tel", Date.valueOf(LocalDate.now()), Courses.BIOMEDICINA, "cpf");
        when(mockSelectTable.buscarPorMatricula(matricula)).thenReturn(mockStudentData);

        studentUser.showMenu();

        verify(mockSelectTable).buscarPorMatricula(matricula);
        verify(mockPrintStudent).imprimirAluno(mockStudentData);
        assertTrue(testOut.toString().contains("=== MENU STUDENT ==="));
    }

    @Test
    void showMenuOption1ShouldHandleStudentNotFound() {
        provideInput("1\n0\n");
        when(mockSelectTable.buscarPorMatricula(matricula)).thenReturn(null);

        studentUser.showMenu();

        verify(mockSelectTable).buscarPorMatricula(matricula);
        verify(mockPrintStudent, never()).imprimirAluno(any());
        assertTrue(testOut.toString().contains("Aluno não encontrado."));
    }

    @Test
    void showMenuOption2ShouldListStudentClasses() {
        provideInput("2\n0\n");
        Integer studentId = 1;
        List<Class> turmas = new ArrayList<>();
        Class turmaDireito = new Class(1, "Turma X", Courses.DIREITO);
        turmas.add(turmaDireito);

        when(mockAlunoDAO.getIdByCpf(matricula)).thenReturn(studentId);
        when(mockTurmaDAO.listarTurmasDoAluno(studentId)).thenReturn(turmas);

        studentUser.showMenu();

        verify(mockAlunoDAO).getIdByCpf(matricula);
        verify(mockTurmaDAO).listarTurmasDoAluno(studentId);
        assertTrue(testOut.toString().contains("=== Suas Turmas ==="));
        assertTrue(testOut.toString().contains("Turma X"));
        assertTrue(testOut.toString().contains(Courses.DIREITO.getNomeCurso()));
    }

    @Test
    void showMenuOption2ShouldHandleNoClasses() {
        provideInput("2\n0\n");
        Integer studentId = 1;
        when(mockAlunoDAO.getIdByCpf(matricula)).thenReturn(studentId);
        when(mockTurmaDAO.listarTurmasDoAluno(studentId)).thenReturn(new ArrayList<>());

        studentUser.showMenu();
        assertTrue(testOut.toString().contains("Você não está matriculado em nenhuma turma."));
    }

    @Test
    void showMenuOption2ShouldHandleStudentIdNotFoundForClasses() {
        provideInput("2\n0\n");
        when(mockAlunoDAO.getIdByCpf(matricula)).thenReturn(null);

        studentUser.showMenu();
        assertTrue(testOut.toString().contains("Aluno não encontrado."));
        verify(mockTurmaDAO, never()).listarTurmasDoAluno(anyInt());
    }

    @Test
    void showMenuOption0ShouldExit() {
        provideInput("0\n");
        studentUser.showMenu();
        verify(mockSelectTable, never()).buscarPorMatricula(anyString());
    }

    @Test
    void showMenuInvalidOptionShouldShowMessage() {
        provideInput("9\n0\n");
        studentUser.showMenu();
        assertTrue(testOut.toString().contains("Opção inválida."));
    }
}