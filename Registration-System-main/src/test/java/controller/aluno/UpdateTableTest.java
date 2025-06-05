package controller.aluno;

import model.Courses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ConnectionFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTableTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;

    private UpdateTable updateTable;

    @BeforeEach
    void setUp() {
        updateTable = new UpdateTable();
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @AfterEach
    void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    void updateTableShouldUpdateNameSuccessfully() {
        provideInput("M123\n1\nNovo Nome\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            updateTable.updateTable();

            verify(mockPreparedStatement).setString(1, "NOVO NOME");
            verify(mockPreparedStatement).setString(2, "M123");
            assertTrue(testOut.toString().contains("Field updated successfully!"));
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void updateTableShouldUpdateTelefoneSuccessfully() {
        provideInput("M123\n2\n987654321\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            updateTable.updateTable();

            verify(mockPreparedStatement).setString(1, "987654321");
            verify(mockPreparedStatement).setString(2, "M123");
            assertTrue(testOut.toString().contains("Field updated successfully!"));
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void updateTableShouldUpdateDataNascimentoSuccessfully() {
        provideInput("M123\n3\n2000-01-01\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            updateTable.updateTable();

            verify(mockPreparedStatement).setDate(1, Date.valueOf("2000-01-01"));
            verify(mockPreparedStatement).setString(2, "M123");
            assertTrue(testOut.toString().contains("Field updated successfully!"));
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void updateTableShouldUpdateCursoSuccessfully() {
        provideInput("M123\n4\n1\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class);
             MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            coursesMock.when(() -> Courses.getById(1)).thenReturn(Courses.ENGENHARIA_DE_SOFTWARE);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            updateTable.updateTable();

            verify(mockPreparedStatement).setString(1, Courses.ENGENHARIA_DE_SOFTWARE.getNomeCurso());
            verify(mockPreparedStatement).setString(2, "M123");
            assertTrue(testOut.toString().contains("Field updated successfully!"));
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void updateTableShouldHandleInvalidCourseId() {
        provideInput("M123\n4\n99\n"); // 99 is an invalid course ID
        try (MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
            coursesMock.when(() -> Courses.getById(99)).thenReturn(null);

            updateTable.updateTable();

            assertTrue(testOut.toString().contains("Invalid course id. Try again."));
            verify(mockPreparedStatement, never()).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateTableShouldUpdateCpfSuccessfully() {
        provideInput("M123\n5\n11122233344\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            updateTable.updateTable();

            verify(mockPreparedStatement).setString(1, "11122233344");
            verify(mockPreparedStatement).setString(2, "M123");
            assertTrue(testOut.toString().contains("Field updated successfully!"));
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void updateTableShouldShowMessageWhenMatriculaNotFound() {
        provideInput("M999\n1\nNovo Nome\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            updateTable.updateTable();

            assertTrue(testOut.toString().contains("No student was found with that enrollment number."));
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void updateTableShouldHandleInvalidOption() {
        provideInput("M123\n9\n");
        updateTable.updateTable();
        assertTrue(testOut.toString().contains("Invalid option. Try again.!"));
    }

    @Test
    void updateTableShouldHandleSQLException() {
        provideInput("M123\n1\nNovo Nome\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB Error"));

            updateTable.updateTable();
            assertTrue(testOut.toString().contains("Error updating field"));
        } catch (SQLException e) {
            fail(e);
        }
    }
}