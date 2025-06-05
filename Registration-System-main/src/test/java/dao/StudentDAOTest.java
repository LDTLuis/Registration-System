package dao;

import model.Courses;
import model.StudentData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private StudentDAO studentDAO;

    private StudentData sampleStudentData;

    @BeforeEach
    void setUp() throws SQLException {
        sampleStudentData = new StudentData(
                "Test Student",
                "1234567890",
                "1234567890",
                Date.valueOf(LocalDate.of(2000, 1, 1)),
                Courses.ENGENHARIA_DE_SOFTWARE,
                "12345678900"
        );
    }

    @Test
    void createTableShouldExecuteCorrectSQL() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            studentDAO.createTable();

            verify(mockStatement).executeUpdate(argThat(sql -> sql.contains("CREATE TABLE IF NOT EXISTS alunos")));
            verify(mockConnection).close();
            verify(mockStatement).close();

        } catch (SQLException e) {
            fail("SQLException not expected", e);
        }
    }

    @Test
    void createTableShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenThrow(new SQLException("Test DB error"));

            assertDoesNotThrow(() -> studentDAO.createTable());


        } catch (SQLException e) {
            fail("Setup SQLException not expected", e);
        }
    }


    @Test
    void insertShouldInsertStudentAndUserCorrectly() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);

            when(mockConnection.prepareStatement(contains("INSERT INTO alunos"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(1);

            PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
            when(mockConnection.prepareStatement(contains("UPDATE alunos SET matricula"))).thenReturn(mockUpdateStmt);

            PreparedStatement mockUserStmt = mock(PreparedStatement.class);
            when(mockConnection.prepareStatement(contains("INSERT INTO usuarios"))).thenReturn(mockUserStmt);

            studentDAO.insert(sampleStudentData);

            verify(mockConnection).setAutoCommit(false);

            ArgumentCaptor<String> stringArgCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Object> dateArgCaptor = ArgumentCaptor.forClass(Object.class);

            verify(mockPreparedStatement).setString(1, "TEST STUDENT");
            verify(mockPreparedStatement).setString(2, "1234567890");
            verify(mockPreparedStatement).setObject(3, sampleStudentData.getDataNascimento());
            verify(mockPreparedStatement).setString(4, Courses.ENGENHARIA_DE_SOFTWARE.getNomeCurso());
            verify(mockPreparedStatement).setString(5, "12345678900");
            verify(mockPreparedStatement).executeQuery();

            assertEquals(1, sampleStudentData.getId());
            assertNotNull(sampleStudentData.getMatricula());
            assertTrue(sampleStudentData.getMatricula().startsWith(String.valueOf(LocalDate.now().getYear())));
            assertTrue(sampleStudentData.getMatricula().contains(String.format("%02d", Courses.ENGENHARIA_DE_SOFTWARE.getId())));
            assertTrue(sampleStudentData.getMatricula().endsWith(String.format("%04d", 1)));


            verify(mockUpdateStmt).setString(1, sampleStudentData.getMatricula());
            verify(mockUpdateStmt).setInt(2, 1);
            verify(mockUpdateStmt).executeUpdate();


            verify(mockUserStmt).setString(1, sampleStudentData.getMatricula());
            verify(mockUserStmt).setString(2, "12345678900");
            verify(mockUserStmt).setString(3, "controller/aluno");
            verify(mockUserStmt).setString(4, sampleStudentData.getMatricula());
            verify(mockUserStmt).executeUpdate();

            verify(mockConnection).commit();
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
            verify(mockUpdateStmt).close();
            verify(mockUserStmt).close();

        } catch (SQLException e) {
            fail("SQLException not expected", e);
        }
    }

    @Test
    void insertShouldRollbackIfIdNotGenerated() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            studentDAO.insert(sampleStudentData);

            verify(mockConnection).setAutoCommit(false);
            verify(mockConnection).rollback();
            verify(mockConnection, never()).commit();
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();

        } catch (SQLException e) {
            fail("SQLException not expected", e);
        }
    }

    @Test
    void insertShouldHandleSQLExceptionDuringInsert() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(contains("INSERT INTO alunos"))).thenThrow(new SQLException("DB insert error"));

            assertDoesNotThrow(() -> studentDAO.insert(sampleStudentData));
            verify(mockConnection, never()).commit();


        } catch (SQLException e) {
            fail("Setup SQLException not expected", e);
        }
    }


    @Test
    void getIdByCpfShouldReturnIdWhenCpfExists() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(contains("SELECT id FROM alunos WHERE cpf = ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(10);

            Integer id = studentDAO.getIdByCpf("12345678900");

            assertNotNull(id);
            assertEquals(10, id);
            verify(mockPreparedStatement).setString(1, "12345678900");
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();

        } catch (SQLException e) {
            fail("SQLException not expected", e);
        }
    }

    @Test
    void getIdByCpfShouldReturnNullWhenCpfNotExists() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(contains("SELECT id FROM alunos WHERE cpf = ?"))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Integer id = studentDAO.getIdByCpf("00000000000");

            assertNull(id);
            verify(mockPreparedStatement).setString(1, "00000000000");
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();

        } catch (SQLException e) {
            fail("SQLException not expected", e);
        }
    }

    @Test
    void getIdByCpfShouldReturnNullOnSQLException() {
        try (MockedStatic<ConnectionFactory> mockedConnectionFactory = mockStatic(ConnectionFactory.class)) {
            mockedConnectionFactory.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            Integer id = studentDAO.getIdByCpf("12345678900");
            assertNull(id);

        } catch (SQLException e) {
            fail("Setup SQLException not expected", e);
        }
    }
}