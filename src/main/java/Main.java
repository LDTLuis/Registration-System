import text.LoginInterface;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        ConnectionFactory cf = new ConnectionFactory();
        LoginInterface loginInterface = new LoginInterface();

        Connection conn = cf.getConnection();
        try {
            Statement stmt = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loginInterface.showMenu();
    }
}
