package finance_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_handler {

    private static final String DATABASE_PATH = "jdbc:sqlite:finance_management.db";

    public static Connection connect() {
        Connection cursor;
        try {
            cursor = DriverManager.getConnection(DATABASE_PATH);
        } catch (SQLException error) {
            throw new RuntimeException(error);
        }
        createTables(cursor);
        return cursor;
    }

    private static void createTables(Connection cursor) {

        String sql_users = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL DEFAULT (lower(hex(randomblob(16)))),
                name TEXT NOT NULL UNIQUE,
                passwd TEXT NOT NULL,
                wallet INT NOT NULL DEFAULT 0
            );
            """;

        String sql_categories = """
            CREATE TABLE categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL DEFAULT (lower(hex(randomblob(16)))),
                user_uuid TEXT NOT NULL,
                name TEXT NOT NULL,
                limit_value INT,
                FOREIGN KEY (user_uuid) REFERENCES users (uuid)
            );
            """;

        String sql_transactions = """
            CREATE TABLE transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category_uuid TEXT NOT NULL,
                value INT NOT NULL,
                income BOOLEAN NOT NULL,
                FOREIGN KEY (category_uuid) REFERENCES users (uuid)
            );
            """;

        try (Statement stmt = cursor.createStatement()) {
            stmt.execute(sql_users);
            stmt.execute(sql_categories);
            stmt.execute(sql_transactions);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
