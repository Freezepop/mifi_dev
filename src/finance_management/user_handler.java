package finance_management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public interface user_handler {
    static void createUser(Connection cursor, String name, String passwd) throws SQLException {
        String insertSQL = "INSERT INTO users (name, passwd) VALUES (?, ?)";

        try (PreparedStatement pstmt = cursor.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, passwd);
            pstmt.executeUpdate();
        }
    }
    static int getUser(Connection cursor, String name, String passwd) throws SQLException {
        String insertSQL = "SELECT name, passwd FROM users WHERE name = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.getString("name") == null) {
                return 0;
            }
            else if (Objects.equals(rs.getString("name"), name) && Objects.equals(rs.getString("passwd"), passwd)) {
                return 1;
            }
            else {
                return 2;
            }
        }
    }
}
