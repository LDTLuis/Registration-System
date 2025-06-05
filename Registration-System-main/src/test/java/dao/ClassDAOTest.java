package dao;

import model.Class;
import model.Courses;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassDAOTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private ClassDAO classDAO;

    @Test
    void criarTabelasTurmaEAlunoTurmaShouldExecuteCorrectSQL() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            classDAO.criarTabelasTurmaEAlunoTurma();

            verify(mockStatement).execute(startsWith("CREATE TABLE IF NOT EXISTS turmas"));
            verify(mockStatement).execute(startsWith("CREATE TABLE IF NOT EXISTS alunos_turmas"));
            verify(mockConnection).close();
            verify(mockStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void criarTabelasTurmaEAlunoTurmaShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenThrow(new SQLException("DB error"));

            assertDoesNotThrow(() -> classDAO.criarTabelasTurmaEAlunoTurma());
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void inserirTurmaShouldExecuteCorrectSQL() {
        Class turma = new Class("Nova Turma", Courses.DIREITO);
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement); // For criarTabelas
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            classDAO.inserirTurma(turma);

            verify(mockPreparedStatement).setString(1, "Nova Turma");
            verify(mockPreparedStatement).setString(2, Courses.DIREITO.getNomeCurso());
            verify(mockPreparedStatement).executeUpdate();
            verify(mockConnection, times(2)).close(); // Once for criarTabelas, once for insert
            verify(mockPreparedStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void inserirTurmaShouldHandleSQLExceptionOnInsert() {
        Class turma = new Class("Turma Problemática", Courses.FISICA);
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement); // For criarTabelas
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Insert failed"));

            assertDoesNotThrow(() -> classDAO.inserirTurma(turma));
            verify(mockConnection, times(2)).close();
        } catch (SQLException e) {
            fail(e);
        }
    }


    @Test
    void inserirAlunoNaTurmaShouldExecuteCorrectSQL() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            classDAO.inserirAlunoNaTurma(1, 5);

            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).setInt(2, 5);
            verify(mockPreparedStatement).executeUpdate();
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
        } catch (SQLException e) {
            fail(e);
        }
    }
    @Test
    void inserirAlunoNaTurmaShouldHandleSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Error inserting student into class"));

            assertDoesNotThrow(() -> classDAO.inserirAlunoNaTurma(1, 1));
            verify(mockConnection).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void listarTodasTurmasShouldReturnListOfClasses() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getInt("id")).thenReturn(1, 2);
            when(mockResultSet.getString("nome")).thenReturn("Turma A", "Turma B");
            when(mockResultSet.getString("curso"))
                    .thenReturn(Courses.ENGENHARIA_DE_SOFTWARE.getNomeCurso(), Courses.MEDICINA.getNomeCurso());

            try (MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
                coursesMock.when(() -> Courses.getByNomeCurso(Courses.ENGENHARIA_DE_SOFTWARE.getNomeCurso()))
                        .thenReturn(Courses.ENGENHARIA_DE_SOFTWARE);
                coursesMock.when(() -> Courses.getByNomeCurso(Courses.MEDICINA.getNomeCurso()))
                        .thenReturn(Courses.MEDICINA);


                List<Class> turmas = classDAO.listarTodasTurmas();

                assertNotNull(turmas);
                assertEquals(2, turmas.size());
                assertEquals("Turma A", turmas.get(0).getNome());
                assertEquals(Courses.ENGENHARIA_DE_SOFTWARE, turmas.get(0).getCurso());
                assertEquals("Turma B", turmas.get(1).getNome());
                assertEquals(Courses.MEDICINA, turmas.get(1).getCurso());

                verify(mockConnection).close();
                verify(mockStatement).close();
                verify(mockResultSet).close();
            }

        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void listarTurmasPorCursoShouldReturnFilteredList() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("nome")).thenReturn("Eng Comp 1");
            // getByNomeCurso is implicitly tested via the parameter

            List<Class> turmas = classDAO.listarTurmasPorCurso(Courses.CIENCIA_DA_COMPUTACAO);

            assertNotNull(turmas);
            assertEquals(1, turmas.size());
            assertEquals("Eng Comp 1", turmas.get(0).getNome());
            assertEquals(Courses.CIENCIA_DA_COMPUTACAO, turmas.get(0).getCurso());
            verify(mockPreparedStatement).setString(1, Courses.CIENCIA_DA_COMPUTACAO.getNomeCurso());
            verify(mockConnection).close();
            verify(mockPreparedStatement).close();
            verify(mockResultSet).close();
        } catch (SQLException e) {
            fail(e);
        }
    }

    @Test
    void listarTurmasDoAlunoShouldReturnCorrectClasses() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("id")).thenReturn(10);
            when(mockResultSet.getString("nome")).thenReturn("Algoritmos Avançados");
            when(mockResultSet.getString("curso")).thenReturn(Courses.SISTEMA_DA_INFORMCAO.getNomeCurso());

            try (MockedStatic<Courses> coursesMock = mockStatic(Courses.class)) {
                coursesMock.when(() -> Courses.getByNomeCurso(Courses.SISTEMA_DA_INFORMCAO.getNomeCurso()))
                        .thenReturn(Courses.SISTEMA_DA_INFORMCAO);

                List<Class> turmas = classDAO.listarTurmasDoAluno(5); // alunoId = 5

                assertNotNull(turmas);
                assertEquals(1, turmas.size());
                assertEquals(10, turmas.get(0).getId());
                assertEquals("Algoritmos Avançados", turmas.get(0).getNome());
                assertEquals(Courses.SISTEMA_DA_INFORMCAO, turmas.get(0).getCurso());
                verify(mockPreparedStatement).setInt(1, 5);
                verify(mockConnection).close();
                verify(mockPreparedStatement).close();
                verify(mockResultSet).close();
            }
        } catch (SQLException e) {
            fail(e);
        }
    }
    @Test
    void listarTodasTurmasShouldReturnEmptyListOnSQLException() {
        try (MockedStatic<ConnectionFactory> cf = mockStatic(ConnectionFactory.class)) {
            cf.when(ConnectionFactory::getConnection).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenThrow(new SQLException("DB error"));

            List<Class> turmas = classDAO.listarTodasTurmas();

            assertNotNull(turmas);
            assertTrue(turmas.isEmpty());
        } catch (SQLException e) {
            fail(e);
        }
    }
}