package text;

import dao.UserDAO;
import model.*;

import java.util.Scanner;

public class LoginInterface {

    public void showMenuLogin() {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        while (true) {

            System.out.println("=== Login ===");
            System.out.print("Login: ");
            String login = sc.nextLine();

            System.out.print("Senha: ");
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
                    System.out.println("Performing student functions with enrollment: " + aluno.getMatricula());
                    System.out.println();
                    users.showMenu();
                }

            } else {
                System.out.println("Login or Invalid Password.\n");
            }
        }
    }
}
