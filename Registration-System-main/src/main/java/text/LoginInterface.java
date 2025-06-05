package text;

import dao.UserDAO;
import model.*;

import java.util.Scanner;

public class LoginInterface {

    private UserDAO userDAO;
    private Scanner sc;

    public LoginInterface() {
        this.userDAO = new UserDAO();
        this.sc = new Scanner(System.in);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setScanner(Scanner scanner) {
        this.sc = scanner;
    }

    public void closeScanner(){
        if(this.sc != null){
            this.sc.close();
        }
    }

    public void showMenuLogin() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Login thread interrupted, exiting login menu.");
                break;
            }

            System.out.println("=== Login ===");
            System.out.print("Login: ");
            if (!sc.hasNextLine()) {
                System.err.println("No more input for login. Exiting login menu.");
                break;
            }
            String login = sc.nextLine();

            System.out.print("Senha: ");
            if (!sc.hasNextLine()) {
                System.err.println("No more input for password. Exiting login menu.");
                break;
            }
            String senha = sc.nextLine();

            Users users = userDAO.fazerLogin(login, senha);

            if (users != null) {
                System.out.println("\nSuccessful login!");

                if (users instanceof AdminUser) {
                    System.out.println("Performing administrations of admin ...");
                    System.out.println();
                    users.showMenu();
                } else if (users instanceof StudentUser) {
                    StudentUser aluno = (StudentUser) users;
                    System.out.println("Performing student functions with enrollment: " + aluno.getMatricula()); //
                    System.out.println();
                    users.showMenu();
                }
                break;
            } else {
                System.out.println("Login or Invalid Password.\n");
            }
        }
    }
}