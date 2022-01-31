# subprocessj

## What is this?

1.  You can execute arbitrary OS command in your Java application using `com.kazurayam.subprocessj.Subprocess`.

2.  You can stop a server process which is listening to a specific IP port using `com.kazurayam.subprocessj.ProcessKiller`.

## Motivation

There are many articles that tell how to use [`java.lang.ProcessBuilder`](https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html). For example, I learned ["Baeldung article: Run Shell Command in Java"](https://www.baeldung.com/run-shell-command-in-java). The ProcessBuilder class is a state of the art with rich set of functionalities. But for me it is not very easy to write a program that utilized ProcessBuilder. It involves multi-threading to consume the output streams (STDOUT and STDERR) from subprocess. I do not want to repeat writing it.

So I have made a simple wrapper of ProcessBuilder which exposes a limited subset of its functionalities.

I named this as `subprocjessj` as I meant it to be a homage to the [Subprocess](https://docs.python.org/3/library/subprocess.html) module of Python.

## API

Javadoc is [here](https://kazurayam.github.io/subprocessj/api/index.html).

## Example

### Running a process

You just call `com.kazurayam.subprocessj.Subprocess.run(List<String> command)`. The `run()` will wait for the sub-process to finish, and returns a `com.kazurayam.subprocessj.CompletedProcess` object which contains the return code, STDOUT and STDERR emitted by the sub-process.

    package com.kazurayam.subprocessj;

    import org.junit.jupiter.api.Disabled;
    import org.junit.jupiter.api.Test;

    import java.io.File;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.Arrays;
    import java.util.stream.Collectors;
    import static org.junit.jupiter.api.Assertions.*;

    class SubprocessTest {

        @Test
        void test_ls() throws Exception {
            Subprocess.CompletedProcess cp =
                    new Subprocess()
                            .cwd(new File("."))
                            .run(Arrays.asList("sh", "-c", "ls")
                            );
            assertEquals(0, cp.returncode());
            //println "stdout: ${cp.getStdout()}";
            //println "stderr: ${cp.getStderr()}";
            assertTrue(cp.stdout().size() > 0);
            assertTrue(cp.stdout().contains("src"));
        }

        @Test
        void test_date() throws Exception {
            Subprocess.CompletedProcess cp =
                    new Subprocess().run(Arrays.asList("/bin/date"));
            assertEquals(0, cp.returncode());
            //println "stdout: ${cp.getStdout()}";
            //println "stderr: ${cp.getStderr()}";
            assertTrue(cp.stdout().size() > 0);
            /*
            assertTrue(cp.getStdout().stream()
                    .filter { line ->
                        line.contains("2021")
                    }.collect(Collectors.toList()).size() > 0)
             */
        }

        /**
         * this test method will throw IOException when executed on a CI/CD environment where
         * "git" is not installed. So I disabled this.
         */
        @Disabled
        @Test
        void test_git() throws Exception {
            Subprocess.CompletedProcess cp =
                        new Subprocess()
                                .cwd(new File(System.getProperty("user.home")))
                                .run(Arrays.asList("/usr/local/bin/git", "status"));
            assertEquals(128, cp.returncode());
            //System.out.println(String.format("stdout: %s", cp.getStdout()));
            //System.out.println(String.format("stderr: %s", cp.getStderr()));
            assertTrue(cp.stderr().size() > 0);
            assertEquals(1,
                    cp.stderr().stream()
                            .filter(line -> line.contains("fatal: not a git repository"))
                            .collect(Collectors.toList())
                            .size()
            );
        }

    }

This will emit the following output in the console:

    0
    total 4712
    drwxr-xr-x+  90 kazurayam       staff     2880  7 31 21:01 .
    drwxr-xr-x    6 root            admin      192  1  1  2020 ..
    ...

### Stopping a process

Using `java.lang.ProcessBuilder` class, you can create an `java.lang.Process` in which arbitrary application can run. Suppose you created a process in which a HTTP Server runs. The process will stay running long until you explicitly stop it. But how can you stop that process?

Sometimes I encounter a new HTTP Server fails to start because the IP port is already in use. It tends to happen because I am not careful enough to stop the previous server process which is hanging on the IP port. In such situation, I have to do, on Mac, the following operations:

1.  execute a shell command `$ lsof -i:<port> -P`, to find out the id of the process which is still hanging on the IP port.

2.  execute a shell command `$ kill <processId>`, to stop the process.

3.  once the process is stopped, the IP port is released.

I wanted to automate this command line operation in my Java code. So I developed a Java class [`com.kazurayam.subprocessj.ProcessKiller`](../src/main/java/com/kazurayam/subprocessj/ProcessKiller.java).

See the following sample JUnit 5 test to see how to use the ProcessKiller.

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

@BeforeAll-annotated method starts the [HiThereServer](../src/main/java/com/kazurayam/subprocessj/HiThereServer.java) using `ProcessBuilder`. The process will start and stay running background. The HiThereServer is a simple HTTP server, listens to the IP port 8500.

@Test-annoted method makes an HTTP request to the HiThereServer.

@AfterAll-annotated method shuts down the HiThereServer using the `ProcessKiller`. You specify the IP port 8500. The ProcessKiller will find the process ID of a process which is listening the port 8500, and kill the process.

## links

The artifact is available at the Maven Central repository:

-   <https://mvnrepository.com/artifact/com.kazurayam/subprocessj>

The project’s repository is here

-   [the repository](https://github.com/kazurayam/subprocessj/)
