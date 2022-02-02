package com.kazurayam.subprocessj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.kazurayam.subprocessj.ProcessTerminator.ProcessTerminationResult;

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
        ProcessFinder.ProcessFindingResult fr = ProcessFinder.findPidByListeningPort(PORT);
        if (fr.returncode() == 0) {
            ProcessTerminationResult ptr = ProcessTerminator.killProcessByPid(fr);
            printPTR("test_killProcessByPid", ptr);
            assertEquals(0, ptr.returncode());
        }
    }

    @Test
    public void test_killProcessOnPort()
            throws IOException, InterruptedException
    {
        ProcessTerminationResult ptr = ProcessTerminator.killProcessOnPort(PORT);
        printPTR("test_killProcessOnPort", ptr);
        assertEquals(0, ptr.returncode());
    }

    private void printPTR(String label, ProcessTerminationResult ptr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(ptr.toString());
    }
}
