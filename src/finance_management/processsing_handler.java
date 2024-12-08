package finance_management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public interface processsing_handler {

    static String getUserUUID(Connection cursor, String name) throws SQLException {

        String insertSQL = "SELECT uuid FROM users WHERE name = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("uuid");
        }
    }

    static void writeIncome(Connection cursor, String category_uuid, String value) throws SQLException {

        String sql = "INSERT INTO transactions (category_uuid, value, income) VALUES (?, ?, true)";

        try (PreparedStatement insert_pstmt = cursor.prepareStatement(sql)) {
            insert_pstmt.setString(1, category_uuid);
            insert_pstmt.setString(2, value);
            insert_pstmt.executeUpdate();
        }
    }

    static void writeExpense(Connection cursor, String category_uuid, String value) throws SQLException {

        String sql = "INSERT INTO transactions (category_uuid, value, income) VALUES (?, ?, false)";

        try (PreparedStatement insert_pstmt = cursor.prepareStatement(sql)) {
            insert_pstmt.setString(1, category_uuid);
            insert_pstmt.setString(2, value);
            insert_pstmt.executeUpdate();
        }
    }

    static String checkCategory(Connection cursor, String category, String user_uuid) throws SQLException {

        String insertSQL = "SELECT uuid FROM categories WHERE name = ? AND user_uuid = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(insertSQL)) {
            pstmt.setString(1, category);
            pstmt.setString(2, user_uuid);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("uuid");
        }
    }

    static void createCategory(Connection cursor, String user_uuid, String category_name, String limit_value) throws SQLException {

        String sql = "INSERT INTO categories (user_uuid, name, limit_value) VALUES (?, ?, ?)";

        try (PreparedStatement insert_pstmt = cursor.prepareStatement(sql)) {
            insert_pstmt.setString(1, user_uuid);
            insert_pstmt.setString(2, category_name);
            insert_pstmt.setInt(3, Integer.parseInt(limit_value));
            insert_pstmt.executeUpdate();
        }
    }

    static void updateCategory(Connection cursor, String user_uuid, String category_name, String limit_value) throws SQLException {

        String sql_update = "UPDATE categories SET limit_value = ? WHERE user_uuid = ? AND name = ?";

        try (PreparedStatement update_pstmt = cursor.prepareStatement(sql_update)) {
            update_pstmt.setInt(1, Integer.parseInt(limit_value));
            update_pstmt.setString(2, user_uuid);
            update_pstmt.setString(3, category_name);
            update_pstmt.executeUpdate();
        }
    }

    static void calculateWallet(Connection cursor, String user_uuid) throws SQLException {

        String sql = "SELECT SUM(CASE WHEN t.income THEN t.value ELSE -t.value END) AS balance FROM users u LEFT JOIN categories c ON u.uuid = c.user_uuid LEFT JOIN transactions t ON c.uuid = t.category_uuid WHERE u.uuid = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(sql)) {
            pstmt.setString(1, user_uuid);
            ResultSet rs = pstmt.executeQuery();
            int balance = rs.getInt("balance");

            String sql_update = "UPDATE users SET wallet = ? WHERE uuid = ?";

            try (PreparedStatement update_pstmt = cursor.prepareStatement(sql_update)) {
                update_pstmt.setInt(1, balance);
                update_pstmt.setString(2, user_uuid);
                update_pstmt.executeUpdate();
            }
        }
    }

    static int calculateIncomeTotal(Connection cursor, String user_uuid) throws SQLException {

        String sql = "SELECT SUM(t.value) AS total_income FROM transactions t JOIN categories c ON t.category_uuid = c.uuid JOIN users u ON c.user_uuid = u.uuid WHERE t.income = TRUE AND u.uuid = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(sql)) {
            pstmt.setString(1, user_uuid);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("total_income");
        }
    }

    static List<Object[]> calculateIncomeTotalByCategory(Connection cursor, String user_uuid) throws SQLException {

        String sql = "SELECT c.name AS category_name, SUM(t.value) AS category_income FROM transactions t JOIN categories c ON t.category_uuid = c.uuid JOIN users u ON c.user_uuid = u.uuid WHERE t.income = TRUE AND u.uuid = ? GROUP BY c.name";

        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement pstmt = cursor.prepareStatement(sql)) {
            pstmt.setString(1, user_uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{
                            rs.getString("category_name"),
                            rs.getInt("category_income")});
                }
            }
        }
        if (!result.isEmpty()) {
            return result;
        }
        else {
            return null;
        }
    }

    static int calculateExpenseTotal(Connection cursor, String user_uuid) throws SQLException {

        String sql = "SELECT SUM(t.value) AS total_expense FROM transactions t JOIN categories c ON t.category_uuid = c.uuid JOIN users u ON c.user_uuid = u.uuid WHERE t.income = FALSE AND u.uuid = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(sql)) {
            pstmt.setString(1, user_uuid);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("total_expense");
        }
    }

    static List<Object[]> calculateExpenseTotalByCategory(Connection cursor, String user_uuid) throws SQLException {

        String sql = "SELECT c.name AS category_name, SUM(t.value) AS category_expense, c.limit_value FROM transactions t JOIN categories c ON t.category_uuid = c.uuid JOIN users u ON c.user_uuid = u.uuid WHERE t.income = FALSE AND u.uuid = ? GROUP BY c.name, c.limit_value";

        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement pstmt = cursor.prepareStatement(sql)) {
            pstmt.setString(1, user_uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{
                            rs.getString("category_name"),
                            rs.getInt("category_expense"),
                            rs.getInt("limit_value")});
                }
            }
        }
        if (!result.isEmpty()) {
            return result;
        }
        else {
            return null;
        }
    }

    static int sbp(Connection cursor, String user_uuid, String friend_name, String value) throws SQLException {

        String sql = "SELECT wallet FROM users WHERE uuid = ?";

        try (PreparedStatement pstmt = cursor.prepareStatement(sql)) {
            pstmt.setString(1, user_uuid);
            ResultSet rs = pstmt.executeQuery();
            int current_balance = rs.getInt("wallet");
            if (current_balance >= Integer.parseInt(value)){
                String get_friend_sql = "SELECT uuid FROM users WHERE name = ?";
                try (PreparedStatement pstmt_get_friend = cursor.prepareStatement(get_friend_sql)) {
                    pstmt_get_friend.setString(1, friend_name);
                    ResultSet rs_get_friend = pstmt_get_friend.executeQuery();
                    String friend_uuid = rs_get_friend.getString("uuid");
                    String category_friend_uuid = checkCategory(cursor, "bro_bonus", friend_uuid);
                    String category_my_uuid = checkCategory(cursor, "bro_bonus", user_uuid);
                    if (category_my_uuid == null){
                        createCategory(cursor, user_uuid, "bro_bonus", "-1");
                        category_my_uuid = checkCategory(cursor, "bro_bonus", user_uuid);

                    }
                    if (category_friend_uuid == null) {
                        createCategory(cursor, friend_uuid, "bro_bonus", "-1");
                        category_friend_uuid = checkCategory(cursor, "bro_bonus", friend_uuid);
                    }
                    writeIncome(cursor, category_friend_uuid, value);
                    writeExpense(cursor, category_my_uuid, value);
                    return 1;
                }
            }
            else {
                return 0;
            }
        }
    }
}
