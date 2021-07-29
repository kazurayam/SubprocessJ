package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class SubprocessTest {

    @Test
    void test_ls() throws Exception {
        Subprocess.CompletedProcess cp =
                new Subprocess()
                        .setCurrentDir(new File("."))
                        .process(Arrays.asList("sh", "-c", "ls")
                        );
        assertEquals(0, cp.getReturnCode());
        //println "stdout: ${cp.getStdout()}";
        //println "stderr: ${cp.getStderr()}";
        assertTrue(cp.getStdout().size() > 0);
        assertTrue(cp.getStdout().contains("src"));
    }

    @Test
    void test_date() throws Exception {
        Subprocess.CompletedProcess cp =
                new Subprocess().process(Arrays.asList("/bin/date"));
        assertEquals(0, cp.getReturnCode());
        //println "stdout: ${cp.getStdout()}";
        //println "stderr: ${cp.getStderr()}";
        assertTrue(cp.getStdout().size() > 0);
        /*
        assertTrue(cp.getStdout().stream()
                .filter { line ->
                    line.contains("2021")
                }.collect(Collectors.toList()).size() > 0)
         */
    }

    @Test
    void test_git() throws Exception {
        Subprocess.CompletedProcess cp =
                new Subprocess()
                        .setCurrentDir(new File(System.getProperty("user.home")))
                        .process(Arrays.asList("/usr/local/bin/git", "status")
                        );
        assertEquals(128, cp.getReturnCode());
        //System.out.println(String.format("stdout: %s", cp.getStdout()));
        //System.out.println(String.format("stderr: %s", cp.getStderr()));
        assertTrue(cp.getStderr().size() > 0);
        assertEquals(1,
                cp.getStderr().stream()
                        .filter(line -> line.contains("fatal: not a git repository"))
                        .collect(Collectors.toList())
                        .size()
        );
    }
}
