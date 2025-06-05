package controller.aluno;

import model.Courses;
import model.StudentData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectTableTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private SelectTable selectTable;

    @Test
    void buscarAlunoShouldReturnListOfStudentsByName() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class);
             MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(startsWith("SELECT * FROM alunos WHERE nome LIKE ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getString("nome")).thenReturn("John Doe");
            when(mockResultSet.getString("matricula")).thenReturn("M123");
            when(mockResultSet.getString("telefone")).thenReturn("12345");
            when(mockResultSet.getDate("data_nascimento")).thenReturn(Date.valueOf(LocalDate.of(2000,1,1)));
            when(mockResultSet.getString("curso")).thenReturn("ENGENHARIA_DE_SOFTWARE");
            when(mockResultSet.getString("cpf")).thenReturn("111222333");

            coursesMock.when(() -> Courses.valueOf("ENGENHARIA_DE_SOFTWARE")).thenReturn(Courses.ENGENHARIA_DE_SOFTWARE);


            List<StudentData> students = selectTable.buscarAluno("John");

            assertNotNull(students);
            assertEquals(1, students.size());
            assertEquals("John Doe", students.get(0).getNome());
            verify(mockPreparedStatement).setString(1, "%John%");
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarAlunoShouldReturnEmptyListIfNoStudentFoundByName() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(startsWith("SELECT * FROM alunos WHERE nome LIKE ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            List<StudentData> students = selectTable.buscarAluno("NonExistent");

            assertNotNull(students);
            assertTrue(students.isEmpty());
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarAlunoShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            List<StudentData> students = selectTable.buscarAluno("AnyName");
            assertNotNull(students);
            assertTrue(students.isEmpty());
        } catch (SQLException e) {
            fail(e);
        }
    }


    @Test
    void buscarAlunoMatriculaShouldReturnListOfStudentsByMatricula() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class);
             MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(startsWith("SELECT * FROM alunos WHERE matricula LIKE ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getString("nome")).thenReturn("Jane Doe");
            when(mockResultSet.getString("matricula")).thenReturn("M456");
            when(mockResultSet.getString("telefone")).thenReturn("67890");
            when(mockResultSet.getDate("data_nascimento")).thenReturn(Date.valueOf(LocalDate.of(1999,5,5)));
            when(mockResultSet.getString("curso")).thenReturn(Courses.MEDICINA.getNomeCurso());
            when(mockResultSet.getString("cpf")).thenReturn("444555666");

            coursesMock.when(() -> Courses.getByNomeCurso(Courses.MEDICINA.getNomeCurso())).thenReturn(Courses.MEDICINA);

            List<StudentData> students = selectTable.buscarAlunoMatricula("M456");

            assertNotNull(students);
            assertEquals(1, students.size());
            assertEquals("M456", students.get(0).getMatricula());
            verify(mockPreparedStatement).setString(1, "%M456%");
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarAlunoMatriculaShouldReturnEmptyListIfNoStudentFoundByMatricula() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(startsWith("SELECT * FROM alunos WHERE matricula LIKE ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            List<StudentData> students = selectTable.buscarAlunoMatricula("M000");

            assertNotNull(students);
            assertTrue(students.isEmpty());
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarAlunoMatriculaShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            List<StudentData> students = selectTable.buscarAlunoMatricula("AnyMatricula");
            assertNotNull(students);
            assertTrue(students.isEmpty());
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarPorMatriculaShouldReturnStudentDataWhenFound() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class);
             MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(startsWith("SELECT * FROM alunos WHERE matricula = ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("nome")).thenReturn("Specific Student");
            when(mockResultSet.getString("matricula")).thenReturn("M789");
            when(mockResultSet.getString("telefone")).thenReturn("101112");
            when(mockResultSet.getDate("data_nascimento")).thenReturn(Date.valueOf(LocalDate.of(2001,10,10)));
            when(mockResultSet.getString("curso")).thenReturn(Courses.DIREITO.getNomeCurso());
            when(mockResultSet.getString("cpf")).thenReturn("777888999");

            coursesMock.when(() -> Courses.getByNomeCursos(Courses.DIREITO.getNomeCurso())).thenReturn(Courses.DIREITO);

            StudentData student = selectTable.buscarPorMatricula("M789");

            assertNotNull(student);
            assertEquals("M789", student.getMatricula());
            verify(mockPreparedStatement).setString(1, "M789");
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarPorMatriculaShouldReturnNullWhenNotFound() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(startsWith("SELECT * FROM alunos WHERE matricula = ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            StudentData student = selectTable.buscarPorMatricula("M000");

            assertNull(student);
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void buscarPorMatriculaShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            StudentData student = selectTable.buscarPorMatricula("AnyMatricula");
            assertNull(student);
        } catch (SQLException e) {
            fail(e);
        }
    }
}