package controller.aluno;

import model.Courses;
import model.StudentData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ConnectionFactory;


import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrintStudentTest {

    private final PrintStream systemOut = System.out;
    private ByteArrayOutputStream testOut;

    @Spy
    @InjectMocks
    private PrintStudent printStudentSpy;


    @BeforeEach
    void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    void restoreSystemOutput() {
        System.setOut(systemOut);
    }

    @Test
    @Disabled("Swing UI testing is complex and requires a different setup or framework")
    void printTableShouldDisplayJFrameWithData() {

    }


    @Test
    void imprimirAlunosShouldPrintAllStudentDetails() {
        List<StudentData> alunos = new ArrayList<>();
        StudentData aluno1 = new StudentData("John Doe", "M123", "111", Date.valueOf(LocalDate.of(2000,1,1)), Courses.ENGENHARIA_DE_SOFTWARE, "123");
        StudentData aluno2 = new StudentData("Jane Smith", "M456", "222", Date.valueOf(LocalDate.of(1999,5,5)), Courses.MEDICINA, "456");
        alunos.add(aluno1);
        alunos.add(aluno2);

        PrintStudent.imprimirAlunos(alunos);

        String output = testOut.toString();
        assertTrue(output.contains("Name: John Doe"));
        assertTrue(output.contains("Enrollment: M123"));
        assertTrue(output.contains("Course: " + Courses.ENGENHARIA_DE_SOFTWARE.getNomeCurso().toUpperCase()));
        assertTrue(output.contains("Name: Jane Smith"));
        assertTrue(output.contains("Enrollment: M456"));
        assertTrue(output.contains("Course: " + Courses.MEDICINA.getNomeCurso().toUpperCase()));
    }

    @Test
    void imprimirAlunosShouldPrintMessageWhenListIsEmpty() {
        List<StudentData> alunos = new ArrayList<>();
        PrintStudent.imprimirAlunos(alunos);
        assertTrue(testOut.toString().contains("No Student found."));
    }

    @Test
    void imprimirAlunoShouldPrintStudentDetails() {
        StudentData aluno = new StudentData("Single Student", "M789", "333", Date.valueOf(LocalDate.of(2001,10,10)), Courses.DIREITO, "789");
        PrintStudent.imprimirAluno(aluno);

        String output = testOut.toString();
        assertTrue(output.contains("Name: Single Student"));
        assertTrue(output.contains("Enrollment: M789"));
        assertTrue(output.contains("Course: " + Courses.DIREITO.getNomeCurso().toUpperCase()));
    }

    @Test
    void imprimirAlunoShouldPrintMessageWhenStudentIsNull() {
        PrintStudent.imprimirAluno(null);
        assertTrue(testOut.toString().contains("Student not found."));
    }

    @Test
    void readTableShouldCallPrintTableWithCorrectSql() {
        doNothing().when(printStudentSpy).printTable(anyString());
        printStudentSpy.readTable();
        verify(printStudentSpy).printTable("SELECT * FROM alunos");
    }
}