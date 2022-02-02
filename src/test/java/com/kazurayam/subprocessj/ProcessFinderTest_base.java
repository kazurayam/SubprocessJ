package com.kazurayam.subprocessj;

import com.kazurayam.subprocessj.ProcessFinder.ProcessFindingResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessFinderTest_base {

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

    private void printPFR(String label, ProcessFindingResult pfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(pfr.toString());
    }
}
