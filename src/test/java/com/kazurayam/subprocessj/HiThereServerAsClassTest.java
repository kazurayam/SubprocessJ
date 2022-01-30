package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HiThereServerAsClassTest {

    private static HiThereServer server;

    @BeforeAll
    static public void beforeAll() throws IOException, InterruptedException {
        server = new HiThereServer();
    }

    @Test
    public void test_request_response() throws IOException {
        URL url = new URL("http://127.0.0.1:8500/");
        URLConnection conn = url.openConnection();
        String content = TestUtils.readInputStream(conn.getInputStream());
        System.out.println(content);
        assert content.contains("Hi there!");
    }

    @AfterAll
    static public void afterAll() {
        server.shutdown();
    }

}
