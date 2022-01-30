package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class HiThereServerTest {

    private static Process process;

    @BeforeAll
    static public void beforeAll() throws IOException, InterruptedException {
        List<String> args = Arrays.asList(
                "java",
                "-cp", "build/classes/java/main",
                "com.kazurayam.subprocessj.HiThereServer"
        );
        ProcessBuilder pb = new ProcessBuilder(args);
        process = pb.start();
        Thread.sleep(1000);
    }

    @Test
    public void test_request_response() throws IOException {
        URL url = new URL("http://127.0.0.1:8500/");
        URLConnection conn = url.openConnection();
        String content = readInputStream(conn.getInputStream());
        assertEquals("Hi there!", content.trim());
    }

    @AfterAll
    static public void afterAll() {
        process.destroy();
    }

    /**
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static String readInputStream(InputStream is) throws IOException {
        BufferedReader r =
                new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

}
