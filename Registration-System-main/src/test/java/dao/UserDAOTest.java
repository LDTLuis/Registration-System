package dao;

import model.AdminUser;
import model.StudentUser;
import model.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private UserDAO userDAO;

    @Test
    void fazerLoginShouldReturnAdminUserWhenCredentialsAreValidAdmin() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("tipo")).thenReturn("ADMIN");

            Users user = userDAO.fazerLogin("admin", "pass");

            assertNotNull(user);
            assertTrue(user instanceof AdminUser);
            verify(mockPreparedStatement).setString(1, "admin");
            verify(mockPreparedStatement).setString(2, "pass");
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void fazerLoginShouldReturnStudentUserWhenCredentialsAreValidAluno() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("tipo")).thenReturn("ALUNO");
            when(mockResultSet.getString("matricula")).thenReturn("M123");

            Users user = userDAO.fazerLogin("aluno1", "senha123");

            assertNotNull(user);
            assertTrue(user instanceof StudentUser);
            assertEquals("M123", ((StudentUser) user).getMatricula());
            verify(mockPreparedStatement).setString(1, "aluno1");
            verify(mockPreparedStatement).setString(2, "senha123");
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void fazerLoginShouldReturnNullWhenCredentialsAreInvalid() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Users user = userDAO.fazerLogin("invalid", "user");

            assertNull(user);
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void fazerLoginShouldReturnNullOnSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

            Users user = userDAO.fazerLogin("user", "pass");
            assertNull(user);
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void createTableShouldExecuteCorrectSQL() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            userDAO.createTable();

            verify(mockStatement).executeUpdate(argThat(sql -> sql.contains("CREATE TABLE IF NOT EXISTS usuarios")));
            verify(mockConnection).close();
            verify(mockStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void createTableShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenThrow(new SQLException("DB error on create"));

            assertDoesNotThrow(() -> userDAO.createTable());
        } catch (SQLException e) {
            fail(e);
        }
    }


    @Test
    void insertUserShouldExecuteCorrectSQL() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            userDAO.insertUser("newUser", "newPass", "ALUNO", "M456");

            verify(mockPreparedStatement).setString(1, "newUser");
            verify(mockPreparedStatement).setString(2, "newPass");
            verify(mockPreparedStatement).setString(3, "ALUNO");
            verify(mockPreparedStatement).setString(4, "M456");
            verify(mockPreparedStatement).executeUpdate();
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void insertUserShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error on insert"));

            assertDoesNotThrow(() -> userDAO.insertUser("test", "test", "test", "test"));
        } catch (SQLException e) {
            fail(e);
        }
    }
}