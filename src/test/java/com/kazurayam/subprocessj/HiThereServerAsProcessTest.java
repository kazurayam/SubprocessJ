package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Start up a process in which HiThereServer runs on background,
 * will use java.lang.ProcessBuilder to create the subprocess.
 * Make an HTTP request and check the response.
 * Shutdown the process of HiThereServer.
 */
public class HiThereServerAsProcessTest {

    @BeforeAll
    static public void beforeAll() throws IOException, InterruptedException {
        List<String> args = Arrays.asList(
                "java",
                "-cp", "build/classes/java/main",
                "com.kazurayam.subprocessj.HiThereServer"
        );
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();
    }

    @Test
    public void test_request_response() throws IOException {
        URL url = new URL("http://127.0.0.1:8500/");
        URLConnection conn = url.openConnection();
        String content = TestUtils.readInputStream(conn.getInputStream());
        assertEquals("Hi there!", content.trim());
    }

    @AfterAll
    static public void afterAll() throws IOException, InterruptedException {
        Long processId = ProcessKiller.killProcessOnPort(8500);
    }


}
