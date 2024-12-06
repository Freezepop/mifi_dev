package sls;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class get_url_list {
    static class GetUrlList implements HttpHandler {
        private final Connection cursor;

        public GetUrlList(Connection cursor) {
            this.cursor = cursor;
        }

        public void handle(HttpExchange exchange) throws IOException {
            String received_uuid = exchange.getRequestURI().getPath().split("/")[2];

            List<Object[]> results;
            try {
                results = getLinksData(received_uuid);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (results != null) {
                StringBuilder responseBuilder = new StringBuilder();
                for (Object[] result : results) {
                    String link_short = (String) result[0];
                    String link_original = (String) result[1];
                    long link_created = (long) result[2];
                    int following_limit = (int) result[3];

                    responseBuilder.append(String.format(
                            "Short Link: %s, Original Link: %s, Created time: %d, Limit: %d\n",
                            link_short, link_original, link_created, following_limit
                    ));
                }

                String response = responseBuilder.toString();

                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                String response = String.format("Извините, но для UUID: %s не было найдено ни одной ссылки. Попробуйте их создать методом reg_short_link", received_uuid);
                exchange.sendResponseHeaders(404, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }

        private List<Object[]> getLinksData(String received_uuid) throws SQLException {
            String query = "SELECT * FROM links WHERE uuid = ?";
            List<Object[]> result = new ArrayList<>();
            try (PreparedStatement pstmt = cursor.prepareStatement(query)) {
                pstmt.setString(1, received_uuid);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(new Object[]{
                                rs.getString("link_short"),
                                rs.getString("link_original"),
                                rs.getLong("time_created"),
                                rs.getInt("following_limit")});
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
    }
}
