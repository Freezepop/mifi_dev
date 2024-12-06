package sls;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws IOException {

        Connection cursor = DB_handler.connect();

        HttpServer server = HttpServer.create(new InetSocketAddress(10070), 0);

        server.createContext("/reg_short_link/", new reg_handler.GetShortLink(cursor));
        server.createContext("/goto/", new follower.GoToResource(cursor));
        server.createContext("/links_list/", new get_url_list.GetUrlList(cursor));
        server.createContext("/delete_link/", new delete_link.DeleteLink(cursor));

        server.setExecutor(null);
        server.start();
    }
}
