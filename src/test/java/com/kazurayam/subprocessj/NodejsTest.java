package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This test runs a javascript "hello.js" on Node.js in a subprocess.
 * It waits for the subprocess to finish.
 * It reads and consumes the stream of stderr of the subprocess to print in the console.
 * It consumes the stdout as well.
 */
public class NodejsTest {

    private Path scriptPath =
            Paths.get(".").resolve("src/test/js/hello.js");
    @Test
    public void test_run_javascript_using_node_command() {
        CommandLocator.CommandLocatingResult clr =
                CommandLocator.find("node");
        //System.out.println(clr.toString());

        if (clr.returncode() == 0) {
            Subprocess.CompletedProcess cp;
            try {
                // You are supposed to specify the "node" command in full path
                // such as "/Users/kazurayam/.nodebrew/current/bin/node"
                cp = new Subprocess()
                        .run(Arrays.asList(clr.command(), scriptPath.toString()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            cp.stderr().forEach(System.err::println);
            cp.stdout().forEach(System.out::println);
            assertEquals(0, cp.returncode());
        } else {
            Subprocess sp = new Subprocess();
            String pathValue = sp.environment("PATH");
            fail(String.format(
                    "the node command was not found in the PATH. " +
                            "Environment Variable PATH = %s",
                    pathValue));
        }
    }
}
