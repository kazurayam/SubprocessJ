package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import com.kazurayam.subprocessj.ProcessFinder.FindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessIdentifierTest {

    private static HiThereServer server;
    private static final int PORT = 8090;

    @BeforeAll
    public static void beforeAll() throws IOException {
        server = new HiThereServer();
        server.setPort(PORT);
        server.startup();
    }

    @AfterAll
    public static void afterAll() {
        server.shutdown();
    }

    @Test
    void test_getCurrentJvmPid() {
        long jvmProcessId = ProcessFinder.findCurrentJvmPid();
        assertTrue(jvmProcessId > 0);
    }

    @Test
    void test_findProcessIdByListeningPort_found()
            throws IOException, InterruptedException
    {
        FindingResult result = ProcessFinder.findPidByListeningPort(PORT);
        assertEquals(0, result.returncode());
        assertTrue(result.processId() > 0);
    }

    /**
     * make sure that the HiThereServer is running in the JVM Process on which
     * this test is running.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void test_HiThereServer_is_running_on_the_same_JVM_process()
            throws IOException, InterruptedException
    {
        long jvmProcessId = ProcessFinder.findCurrentJvmPid();
        FindingResult result = ProcessFinder.findPidByListeningPort(PORT);
        assertEquals(jvmProcessId, result.processId());
    }

    @Test
    void test_findProcessIdByListeningPort_notfound() throws IOException, InterruptedException {
        FindingResult result = ProcessFinder.findPidByListeningPort(PORT + 1);
        assertNotEquals(0, result.returncode());
        assertTrue(result.processId() < 0);
        System.err.println(result.message());
    }
}
