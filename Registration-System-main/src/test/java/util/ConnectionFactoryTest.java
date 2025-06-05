package util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConnectionFactoryTest {

    @Test
    void getConnectionShouldReturnConnection() {
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            Connection mockConnection = mock(Connection.class);
            mockedDriverManager.when(() -> DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/postgres", "postgres", "010203"))
                    .thenReturn(mockConnection);

            Connection conn = ConnectionFactory.getConnection();
            assertNotNull(conn);
            assertSame(mockConnection, conn);
        }
    }

    @Test
    void getConnectionShouldThrowRuntimeExceptionOnSQLException() {
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenThrow(new SQLException("Test SQL Exception"));

            Exception exception = assertThrows(RuntimeException.class, ConnectionFactory::getConnection);
            assertTrue(exception.getMessage().contains("Erro ao conectar ao Banco de Dados."));
            assertNotNull(exception.getCause());
            assertTrue(exception.getCause() instanceof SQLException);
            assertEquals("Test SQL Exception", exception.getCause().getMessage());
        }
    }
}