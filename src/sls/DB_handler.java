package sls;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_handler {

    private static final String DATABASE_PATH = "jdbc:sqlite:short_links.db";

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
        String sql_links = """
            CREATE TABLE links (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL,
                link_original TEXT NOT NULL,
                link_short TEXT NOT NULL,
                following_limit INTEGER NOT NULL,
                time_created INTEGER NOT NULL,
                FOREIGN KEY (uuid) REFERENCES users (uuid)
            );
            """;

        String sql_users = """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL DEFAULT (lower(hex(randomblob(16)))),
                name TEXT NOT NULL UNIQUE
            );
            """;

        try (Statement stmt = cursor.createStatement()) {
            stmt.execute(sql_users);
            stmt.execute(sql_links);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

}
