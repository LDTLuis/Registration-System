package controller.aluno;

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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTableTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;

    private DeleteTable deleteTable;

    @BeforeEach
    void setUp() {
        deleteTable = new DeleteTable();
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
    void deleteTableShouldDeleteStudentWhenMatriculaExists() {
        provideInput("M123\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            deleteTable.deleteTable();

            verify(mockPreparedStatement).setString(1, "M123");
            assertTrue(testOut.toString().contains("Student successfully deleted!"));
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void deleteTableShouldShowMessageWhenMatriculaNotFound() {
        provideInput("M999\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            deleteTable.deleteTable();

            verify(mockPreparedStatement).setString(1, "M999");
            assertTrue(testOut.toString().contains("No student was found with that enrollment number."));
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void deleteTableShouldHandleSQLException() {
        provideInput("M789\n");
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

            deleteTable.deleteTable();

            assertTrue(testOut.toString().contains("Error deleting student."));
        } catch (SQLException e) {
            fail(e);
        }
    }
}