package sls;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;


public class reg_handler {
    static class GetShortLink implements HttpHandler {
        private final Connection cursor;

        public GetShortLink(Connection cursor) {
            this.cursor = cursor;
        }

        public void handle(HttpExchange exchange) throws IOException {

            String id = exchange.getRequestURI().getPath().split("/")[2];
            String limit_string = exchange.getRequestURI().getPath().split("/")[3];
            String link_original = exchange.getRequestURI().getQuery();
            int following_limit;
            following_limit = Integer.parseInt(limit_string);
            Instant now = Instant.now();
            long timestampInSeconds = now.getEpochSecond();
            String uuid;
            String link_short;
            String response;

            try {
                uuid = getUserUUID(id);
                try {
                    link_short = shorter.make_shorted_link(link_original, uuid);
                    response = String.format("Пользователь с таким именем уже существует. uuid: %s. Используйте uuid вместе с укороченной ссылкой %s в методе goto (http://127.0.0.1:10070/goto/%s?%s) для редиректа на нужны ресурс: %s", uuid, link_short, link_short, uuid, link_original);
                }
                catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

                if (uuid != null) {
                    String check_link_short = getCheckLinkExists(link_short);
                    if (check_link_short == null) {
                        insertLink(uuid, link_original, link_short, following_limit, timestampInSeconds);
                    }
                }
                else {
                    uuid = createUser(id);
                    try {
                        link_short = shorter.make_shorted_link(link_original, uuid);
                        response = String.format("Пользователь зарегистрирован с uuid %s. Используйте uuid вместе с укороченной ссылкой %s в методе goto (http://127.0.0.1:10070/goto/%s?%s) для редиректа на нужны ресурс: %s", uuid, link_short, link_short, uuid, link_original);
                    }
                    catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    insertLink(uuid, link_original, link_short, following_limit, timestampInSeconds);

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private String getUserUUID(String username) throws SQLException {
            String query = "SELECT uuid FROM users WHERE name = ?";
            try (PreparedStatement pstmt = cursor.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("uuid");
                    }
                    else {
                        return null;
                    }
                }
            }
        }

        private String getCheckLinkExists(String link_short) throws SQLException {
            String query = "SELECT link_short FROM links WHERE link_short = ?";
            try (PreparedStatement pstmt = cursor.prepareStatement(query)) {
                pstmt.setString(1, link_short);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("link_short");
                    }
                    else {
                        return null;
                    }
                }
            }
        }

        private String createUser(String name) throws SQLException {
            String sql = "INSERT INTO users (name) VALUES (?)";
            String uuid = null;
            try (PreparedStatement insert_pstmt = cursor.prepareStatement(sql)) {
                insert_pstmt.setString(1, name);
                insert_pstmt.executeUpdate();
                String query = "SELECT uuid FROM users WHERE name = ?";
                try (PreparedStatement select_pstmt = cursor.prepareStatement(query)) {
                    select_pstmt.setString(1, name);
                    try (ResultSet rs = select_pstmt.executeQuery()) {
                        if (rs.next()) {
                            uuid = rs.getString("uuid");
                        }
                    }
                }
            }
            return uuid;
        }

        private void insertLink(String uuid, String link_original, String link_short, int following_limit, long time_created) throws SQLException {
            String insertSQL = "INSERT INTO links (uuid, link_original, link_short, following_limit, time_created) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = cursor.prepareStatement(insertSQL)) {
                pstmt.setString(1, uuid);
                pstmt.setString(2, link_original);
                pstmt.setString(3, link_short);
                pstmt.setInt(4, following_limit);
                pstmt.setLong(5, time_created);
                pstmt.executeUpdate();
            }
        }
    }
}
