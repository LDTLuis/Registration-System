package text;

import dao.UserDAO;
import model.*;

import java.util.Scanner;

public class LoginInterface {

    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();

        System.out.println("=== Login ===");
        System.out.print("Login: ");
        String login = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        Users users = userDAO.fazerLogin(login, senha);

        if (users != null) {
            System.out.println("\nLogin realizado com sucesso!");

            if (users instanceof Admin) {
                System.out.println("Executando funções de admin...");
                System.out.println();
                users.showMenu();

            } else if (users instanceof StudantUser) {
                StudantUser aluno = (StudantUser) users;
                System.out.println("Executando funções do aluno com matrícula: " + aluno.getMatricula());
                System.out.println();
                users.showMenu();
            }

        } else {
            System.out.println("Login ou senha inválidos.");
        }

    }
}
