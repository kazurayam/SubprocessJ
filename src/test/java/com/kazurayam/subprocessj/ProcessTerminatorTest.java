package com.kazurayam.subprocessj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.kazurayam.subprocessj.ProcessTerminator.TerminationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessTerminatorTest {

    private static int PORT = 8500;

    @BeforeEach
    public void setup() throws IOException, InterruptedException {
        List<String> args = Arrays.asList(
                "java",
                "-cp", "build/classes/java/main",
                "com.kazurayam.subprocessj.HiThereServer"
        );
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();
        Thread.sleep(2000);
    }


    @Test
    public void test_killProcessByPid()
            throws IOException, InterruptedException {
        ProcessFinder.FindingResult fr = ProcessFinder.findPidByListeningPort(PORT);
        if (fr.returncode() == 0) {
            TerminationResult tr = ProcessTerminator.killProcessByPid(fr);
            assertEquals(0, tr.returncode());
        }
    }

    @Test
    public void test_killProcessOnPort()
            throws IOException, InterruptedException
    {
        TerminationResult tr = ProcessTerminator.killProcessOnPort(PORT);
        if (tr.returncode() != 0) {
            System.err.println("tr.message:" + tr.message());
            System.err.println("tr.returncode:" + tr.returncode());
            if (tr.getFindingResult().isPresent()) {
                System.err.println("fr.message:" + tr.getFindingResult().get().message());
                System.err.println("fr.returncode:" + tr.getFindingResult().get().returncode());
            }
        }
        assertEquals(0, tr.returncode(), tr.message());
    }

}
