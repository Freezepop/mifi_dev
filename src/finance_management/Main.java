package finance_management;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        Connection cursor = DB_handler.connect();
        Scanner scanner = new Scanner(System.in);

        new authentication().run(scanner, cursor);

    }
}
