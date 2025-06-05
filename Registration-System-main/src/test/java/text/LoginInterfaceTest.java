package text;

import dao.UserDAO;
import model.AdminUser;
import model.StudentUser;
import model.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginInterfaceTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Mock
    private UserDAO mockUserDAO;
    @Mock
    private AdminUser mockAdminUser;
    @Mock
    private StudentUser mockStudentUser;

    @InjectMocks
    private LoginInterface loginInterface;

    private LoginInterface realLoginInterface;


    @BeforeEach
    void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        realLoginInterface = new LoginInterface();
        realLoginInterface.setUserDAO(mockUserDAO);
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        realLoginInterface.setScanner(new java.util.Scanner(testIn));
    }


    @AfterEach
    void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
        realLoginInterface.closeScanner();
    }

    @Test
    void showMenuLoginShouldAuthenticateAdminAndCallMenu() {
        provideInput("admin\npass\n");
        when(mockUserDAO.fazerLogin("admin", "pass")).thenReturn(mockAdminUser);

        Thread thread = new Thread(() -> realLoginInterface.showMenuLogin());
        thread.setDaemon(true);
        thread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (thread.isAlive()) {
            thread.interrupt();
        }


        assertTrue(testOut.toString().contains("Successful login!"));
        assertTrue(testOut.toString().contains("Performing administrations of admin ..."));
        verify(mockAdminUser, times(1)).showMenu();
    }


    @Test
    void showMenuLoginShouldAuthenticateStudentAndCallMenu() {
        provideInput("student\npass123\n");
        when(mockUserDAO.fazerLogin("student", "pass123")).thenReturn(mockStudentUser);
        when(mockStudentUser.getMatricula()).thenReturn("M123");

        Thread thread = new Thread(() -> realLoginInterface.showMenuLogin());
        thread.setDaemon(true);
        thread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (thread.isAlive()) {
            thread.interrupt();
        }


        assertTrue(testOut.toString().contains("Successful login!"));
        assertTrue(testOut.toString().contains("Performing student functions with enrollment: M123"));
        verify(mockStudentUser, times(1)).showMenu();
    }

    @Test
    void showMenuLoginShouldShowErrorForInvalidCredentials() {
        provideInput("wrong\nuser\n");
        when(mockUserDAO.fazerLogin("wrong", "user")).thenReturn(null);

        Thread thread = new Thread(() -> realLoginInterface.showMenuLogin());
        thread.setDaemon(true);
        thread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (thread.isAlive()) {
            thread.interrupt();
        }

        assertTrue(testOut.toString().contains("Login or Invalid Password."));
        verify(mockAdminUser, never()).showMenu();
        verify(mockStudentUser, never()).showMenu();
    }
}