package text;

import dao.StudentDAO;
import dao.UserDAO;

import java.util.Scanner;

public class CreateInterface {

    Scanner sc = new Scanner(System.in);
    StudentDAO studentDAO = new StudentDAO();
    UserDAO userDAO = new UserDAO();

    public void showMenuCreateTable() {

        System.out.print("Do you want to create the 'admin' and 'students' tables? (Y/N): ");
        String answer = sc.nextLine().trim().toUpperCase();

        if (answer.equals("Y")) {

            studentDAO.createTable();
            userDAO.createTable();

        } else {
            System.out.println("Table creation skipped.");
        }
    }

    public void insertAdminInTable() {

        System.out.print("\nDo you want insert 'admin' in table? (Y/N): ");
        String answer = sc.nextLine().trim().toUpperCase();
        System.out.println();

        if (answer.equals("Y")) {

            userDAO.insertUser("admin1", "1234", "ADMIN", null);
        }

    }
}
