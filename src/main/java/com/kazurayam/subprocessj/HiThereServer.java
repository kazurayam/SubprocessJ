package com.kazurayam.subprocessj;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * A HTTP Server that listens to the IP port #8500.
 * It replies "Hi there!" to any request.
 * This is easily activated by java.lang.ProcessBuilder.
 * This is useful to learn how to utilize ProcessBuilder.
 */
public class HiThereServer {

    private HttpServer server;

    HiThereServer() throws IOException {
        server = HttpServer.create(
                new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(HiThereServer::handleRequest);
        server.start();
    }

    void shutdown() {
        if (server != null) {
            server.stop(0);
        }
    }

    public static void main(String[] args) throws IOException {
        HiThereServer server = new HiThereServer();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String response = content();
        exchange.getResponseHeaders().put("Content-Type", Arrays.asList("text/html"));
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    
    private static String content() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("<title>The HTML5 Herald</title>");
        sb.append("<meta name\"description\" content=\"Hi there!\">\n");
        sb.append("<meta name=\"author\" content=\"kazurayam\">\n");
        sb.append("<!-- <link rel=\"stylesheet\" href=\"css/styles.css?v=1.0\"> -->\n");
        sb.append("<!--[if lt IE 9]>\n");
        sb.append("  <script src=\"https://cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.3/html5shiv.js\"></script>\n");
        sb.append("<![endif]-->\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<h1>Hi there!</h1>\n");
        sb.append("<!-- <script src=\"js/scripts.js\"></script> -->\n");
        sb.append("</body>\n");
        sb.append("</html\n");
        return sb.toString();
    }
}


