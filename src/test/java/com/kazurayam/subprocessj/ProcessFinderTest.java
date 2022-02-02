package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kazurayam.subprocessj.ProcessFinder.ProcessFindingResult;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessFinderTest {

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
    void test_findProcessIdByListeningPort_found() {
        ProcessFindingResult pfr = ProcessFinder.findPidByListeningPort(PORT);
        printPFR("test_findProcessIdByListeningPort_found", pfr);
        assertEquals(0, pfr.returncode(), pfr.message());
        assertTrue(pfr.processId() > 0);
    }

    /**
     * make sure that the HiThereServer is running in the JVM Process on which
     * this test is running.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void test_HiThereServer_is_running_on_this_process() {
        long jvmProcessId = ProcessFinder.findCurrentJvmPid();
        ProcessFindingResult pfr = ProcessFinder.findPidByListeningPort(PORT);
        printPFR("test_HiThereServer_is_running_on_this_process", pfr);
        assertEquals(0, pfr.returncode(), pfr.message());
        assertEquals(jvmProcessId, pfr.processId(), pfr.message());
    }

    @Test
    void test_findProcessIdByListeningPort_notfound() {
        ProcessFindingResult pfr = ProcessFinder.findPidByListeningPort(PORT + 1);
        printPFR("test_findProcessIdByListeningPort_notfound", pfr);
        assertNotEquals(0, pfr.returncode(), pfr.message());
        assertTrue(pfr.processId() < 0);
    }

    @Test
    void test_makeRegexForFilteringWindowsNetstatOutput() {
        String[] data = new String[] {
            "    TCP         0.0.0.0:13688          0.0.0.0:0              LISTENING       4080",
            "    TCP         [::]:13688             [::]:0                 LISTENING       4080"
        };
        // protocol   local-address  exteria-address  state  process-id
        ;
        Matcher m0 = Pattern.compile(ProcessFinder.makeRegexForFilteringWindowsNetstatOutput(13688))
                .matcher(data[0]);
        assertTrue(m0.matches(), data[0] + "\n" + m0.toString());
        assertEquals("0.0.0.0:13688", m0.group(1));
        assertEquals("0.0.0.0", m0.group(2));
        assertEquals("13688", m0.group(3));
        assertEquals("0.0.0.0:0", m0.group(4));
        assertEquals("0.0.0.0", m0.group(5));
        assertEquals("0", m0.group(6));
        assertEquals("LISTENING", m0.group(7));
        assertEquals("4080", m0.group(8));      // process id
        //
        Matcher m1 = Pattern.compile(ProcessFinder.makeRegexForFilteringWindowsNetstatOutput(13688))
                .matcher(data[1]);
        assertFalse(m1.matches(), data[1] + "\n" + m1.toString());
    }

    private void printPFR(String label, ProcessFindingResult pfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(pfr.toString());
    }
}
