package sls;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

public class follower {
    static class GoToResource implements HttpHandler {
        private final Connection cursor;

        public GoToResource(Connection cursor) {
            this.cursor = cursor;
        }

        public void handle(HttpExchange exchange) throws IOException {
            Instant now = Instant.now();
            long current_ts = now.getEpochSecond();

            String received_short_url = exchange.getRequestURI().getPath().split("/")[2];
            String uuid_query = exchange.getRequestURI().getQuery();
            String response;

            try {
                Object[] result = getShortLink(received_short_url);
                if (result != null) {
                    String link_short = (String) result[0];
                    String link_original = (String) result[1];
                    long link_created = (long) result[2];
                    int following_limit = (int) result[3];
                    String uuid = (String) result[4];

                    if (Objects.equals(uuid_query, uuid)) {
                        if (current_ts - link_created >= 86400) {
                            deleteExpiredLink(link_short);
                            response = String.format("Извините, но ваша ссылка %s -> %s просрочилась и была удалена (срок хранения 24 часа). Создайте новую методом reg_short_link", link_short, link_original);
                        } else if (following_limit < 1) {
                            response = String.format("Извините, но ваша ссылка %s -> %s исчерпала лимит переходов. Она будет удалена в течение 24 часов. Удалите текущую ссылку методом delete_link и создайте новую методом reg_short_link\nПосмотреть текущий список ссылок можно методом links_list", link_short, link_original);
                        } else {
                            updateCountFollowing(link_short, following_limit - 1);
                            Desktop.getDesktop().browse(new URI(link_original));
                            response = String.format("Переход на оригинальную ссылку %s успешно выполнен. Осталось %d переходов", link_original, following_limit - 1);
                        }
                    } else {
                        response = "Извините, но пользователя с таким uuid нет. Зарегистрируйтесь методом reg_short_link";
                    }
                } else {
                    response = "Извините, но такой ссылки нет. Сделайте новую методом reg_short_link";
                }
            } catch (SQLException | URISyntaxException e) {
                response = "Произошла ошибка обработки запроса. Попробуйте позже.";
            }

            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private Object[] getShortLink(String link_short) throws SQLException {
            String query = "SELECT link_short, link_original, time_created, following_limit, uuid FROM links WHERE link_short = ?";
            try (PreparedStatement pstmt = cursor.prepareStatement(query)) {
                pstmt.setString(1, link_short);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{rs.getString("link_short"),
                                rs.getString("link_original"),
                                rs.getLong("time_created"),
                                rs.getInt("following_limit"),
                                rs.getString("uuid")};
                    }
                    else {
                        return null;
                    }
                }
            }
        }

        private void deleteExpiredLink(String link_short) throws SQLException {
            String deleteSQL = "DELETE FROM links WHERE link_short = ?";

            try (PreparedStatement pstmt = cursor.prepareStatement(deleteSQL)) {
                pstmt.setString(1, link_short);
                pstmt.executeUpdate();
            }
        }

        private void updateCountFollowing(String link_short, int following_limit) throws SQLException {
            String updateSQL = "UPDATE links SET following_limit = ? WHERE link_short = ?";

            try (PreparedStatement pstmt = cursor.prepareStatement(updateSQL)) {
                pstmt.setInt(1, following_limit);
                pstmt.setString(2, link_short);
                pstmt.executeUpdate();
            }
        }

    }
}
