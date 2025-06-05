package model;

import controller.aluno.DeleteTable;
import controller.aluno.UpdateTable;
import dao.ClassDAO;
import dao.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import text.SelectInterface;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Scanner;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserTest {

    @Mock
    private StudentDAO mockAlunoDAO;
    @Mock
    private ClassDAO mockTurmaDAO;
    @Mock
    private UpdateTable mockUpdateTable;
    @Mock
    private DeleteTable mockDeleteTable;
    @Mock
    private SelectInterface mockSelectInterface;


    private AdminUser adminUser;

    private final InputStream systemIn = System.in;
    private ByteArrayInputStream testIn;

    @BeforeEach
    void setUp() {
        adminUser = new AdminUser("admin", "pass");

        adminUser.setAlunoDAO(mockAlunoDAO);
        adminUser.setTurmaDAO(mockTurmaDAO);
        adminUser.setUpdateTable(mockUpdateTable);
        adminUser.setDeleteTable(mockDeleteTable);
        adminUser.setSelectInterface(mockSelectInterface);
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);

        adminUser.setScanner(new Scanner(System.in));
    }

    @AfterEach
    void restoreSystemInStreams() {
        System.setIn(systemIn);
        adminUser.closeScanner();
    }

    @Test
    void showMenuOption1ShouldAttemptToInsertStudent() {
        String input = "Test Name\n12345\n2000-01-01\n1\n12345678900\n"; // Corresponds to lerDadosAluno
        provideInput("1\n" + input + "0\n"); // Option 1, then data for student, then Option 0 to exit

        StudentData expectedStudentData = new StudentData(
                "Test Name",
                "12345",
                "12345",
                Date.valueOf(LocalDate.of(2000,1,1)),
                Courses.ENGENHARIA_DE_SOFTWARE,
                "12345678900"
        );


        adminUser.showMenu();


        verify(mockAlunoDAO).insert(any(StudentData.class));
    }


    @Test
    void showMenuOption2ShouldCallUpdateTable() {
        provideInput("2\n0\n");
        adminUser.showMenu();
        verify(mockUpdateTable).updateTable();
    }

    @Test
    void showMenuOption3ShouldCallSelectInterfaceShowMenu() {
        provideInput("3\n0\n");
        adminUser.showMenu();
        verify(mockSelectInterface).showMenu();
    }

    @Test
    void showMenuOption4ShouldCallDeleteTable() {
        provideInput("4\n0\n");
        adminUser.showMenu();
        verify(mockDeleteTable).deleteTable();
    }


    @Test
    void showMenuOption5ShouldAttemptToCreateClass() {
        String input = "Turma A\n1\n"; // Nome da Turma, Código do Curso
        provideInput("5\n" + input + "0\n");

        adminUser.showMenu();
        verify(mockTurmaDAO).inserirTurma(any(Class.class));
    }


    @Test
    void showMenuOption6ShouldAttemptToInsertStudentInClass() {
        String input = "12345678900\n1\n"; // CPF, ID Turma
        provideInput("6\n" + input + "0\n");

        when(mockAlunoDAO.getIdByCpf("12345678900")).thenReturn(1);

        adminUser.showMenu();

        verify(mockTurmaDAO).inserirAlunoNaTurma(1,1);
    }


    @Test
    void showMenuOption7ShouldListClasses() {
        provideInput("7\n3\n0\n"); // Option 7 (Listar Turmas), Option 3 (Voltar), Option 0 (Sair do Admin Menu)
        adminUser.showMenu();

    }


    @Test
    void showMenuOption0ShouldExit() {
        provideInput("0\n");
        adminUser.showMenu();

        verify(mockAlunoDAO, never()).insert(any());
        verify(mockUpdateTable, never()).updateTable();

    }

    @Test
    void lerDadosAlunoShouldReturnNullForInvalidCourseId() {
        provideInput("Test Name\n12345\n2000-01-01\n99\n12345678900\n"); // 99 is invalid course
        StudentData result = adminUser.lerDadosAluno();
        assertNull(result);
    }

    @Test
    void lerDadosAlunoShouldReturnNullForInvalidDate() {
        provideInput("Test Name\n12345\nINVALID-DATE\n1\n12345678900\n");
        StudentData result = adminUser.lerDadosAluno();
        assertNull(result);
    }


    @Test
    void criarTurmaShouldHandleInvalidCourseId() {
        provideInput("Nova Turma\n99\n"); // 99 = invalid course
        adminUser.criarTurma();
        verify(mockTurmaDAO, never()).inserirTurma(any(Class.class));
    }


    @Test
    void inserirAlunoEmTurmaPorCpfShouldHandleStudentNotFound() {
        provideInput("nonexistentcpf\n1\n");
        when(mockAlunoDAO.getIdByCpf("nonexistentcpf")).thenReturn(null);
        adminUser.inserirAlunoEmTurmaPorCpf();
        verify(mockTurmaDAO, never()).inserirAlunoNaTurma(anyInt(), anyInt());
    }
}