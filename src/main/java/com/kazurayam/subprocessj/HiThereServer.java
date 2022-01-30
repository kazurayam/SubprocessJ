package com.kazurayam.subprocessj;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * A HTTP Server that listens to the IP port #8500.
 * It replies "Hi there!" to any request.
 * This is easily activated by java.lang.ProcessBuilder.
 * This is useful to learn how to utilize ProcessBuilder.
 */
public class HiThereServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(HiThereServer::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String response = "Hi there!";
        exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}


