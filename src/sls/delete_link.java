package sls;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class delete_link {
    static class DeleteLink implements HttpHandler {
        private final Connection cursor;

        public DeleteLink(Connection cursor) {
            this.cursor = cursor;
        }

        public void handle(HttpExchange exchange) throws IOException {
            String received_link_short = exchange.getRequestURI().getPath().split("/")[2];
            String response;

            try {
                boolean check_delete = deleteLink(received_link_short);
                if (check_delete) {
                    response = String.format("Короткая ссылка %s была удалена.", received_link_short);
                }
                else {
                    response = String.format("Короткая ссылка не была найдена, удалять нечего.", received_link_short);
                }
            } catch (SQLException e) {
                response = String.format("Удаление ссылки %s завершилось неудачно.", received_link_short);
            }

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private boolean deleteLink(String link_short) throws SQLException {
            String deleteSQL = "DELETE FROM links WHERE link_short = ?";

            try (PreparedStatement pstmt = cursor.prepareStatement(deleteSQL)) {
                pstmt.setString(1, link_short);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            }
        }
    }
}
