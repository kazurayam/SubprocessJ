package com.kazurayam.subprocessj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kazurayam.subprocessj.ProcessFinder.FindingResult;

import static org.junit.jupiter.api.Assertions.*;

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

    /**
     * Just lookup Pid that is listening to port 8500
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void look_for_pic_listening_to_8500() throws IOException, InterruptedException{
        FindingResult result = ProcessFinder.findPidByListeningPort(8500);
        if (result.returncode() == 0) {
            System.out.println("pid=" + result.processId() + " listening to the port 8500");
        } else {
            System.err.println("no process found listening tot the port 8500");
        }
    }

    @Test
    void test_findProcessIdByListeningPort_found()
            throws IOException, InterruptedException
    {
        FindingResult result = ProcessFinder.findPidByListeningPort(PORT);
        System.out.println("pid=" + result.processId() + " listening to the port " + PORT);
        assertEquals(0, result.returncode(), result.message());
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
        assertEquals(0, result.returncode(), result.message());
        assertEquals(jvmProcessId, result.processId(), result.message());
    }

    @Test
    void test_findProcessIdByListeningPort_notfound() throws IOException, InterruptedException {
        FindingResult result = ProcessFinder.findPidByListeningPort(PORT + 1);
        assertNotEquals(0, result.returncode(), result.message());
        assertTrue(result.processId() < 0);
        //System.err.println(result.message());
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
}
