package com.kazurayam.subprocessj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * <p>Subprocess object allows you to spawn new OS subprocess using
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html">java.lang.ProcessBuilder</a>. Subprocess#run() waits for the
 * subprocess to finish, returns a CompletedProcess object which contains
 * the return code, STDOUT and STDIN of the executed command.</p>
 *
 * <p>A simple example as a JUnit5 test case:</p>
 * <PRE>
 * import com.kazurayam.subprocessj.Subprocess;
 * import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
 * import org.junit.jupiter.api.Test;
 *
 * class SubprocessTest {
 *     &#64;Test
 *     void test_demo() throws Exception {
 *
 *         Subprocess subprocess = new Subprocess();
 *
 *         // change the current working directory for the process in which a command is executed
 *         subprocess.cwd(new File(System.getProperty("user.home")));
 *
 *         // execute a command in a forked process
 *         CompletedProcess cp = subprocess.run(Arrays.asList("ls", "-la", "."));
 *
 *         // use the return code of the executed command
 *         System.out.println(cp.returncode());
 *
 *         // use the STDOUT of the executed command
 *         cp.stdout().forEach(System.out::println);
 *
 *         // use the STDERR of the executed command
 *         cp.stderr().forEach(System.out::println);
 *     }
 * }
 * </PRE>
 *
 *
 *
 * <p>The following features are supported:</p>
 * <ol>
 * <li>returning the return code from the Subpprocess</li>
 * <li>capturing the STDOUT from the Subprocess</li>
 * <li>capturing the STDERR from the Subprocess</li>
 * <li>starting a subprocess with a modified working directory</li>
 * </ol>
 *
 * <p>The following features are still to be considered:</p>
 * <ol>
 * <li>PIPE: Connecting INPUT from other process as STDIN for the Subprocess</li>
 * <li>Starting a Subprocess with a modified Environment variables</li>
 * <li>inheriting the I/O of the Current Process</li>
 * </ol>
 *
 * @author kazurayam
 * @version 1.0.0
 */
public class Subprocess {

    private File cwd = new File(".");

    public Subprocess() {}

    /**
     * Change the current working directory.
     * As default, will be set as the current working directory of the caller process "."
     * @param currentWorkingDirectory a File object, which is a directory
     * @return the Subprocess object. for functional call chaining.
     */
    public Subprocess cwd(File currentWorkingDirectory) {
        Objects.requireNonNull(currentWorkingDirectory);
        if (! currentWorkingDirectory.exists()) {
            throw new IllegalArgumentException(currentWorkingDirectory.getAbsolutePath() +
                    " does not exist");
        }
        if (! currentWorkingDirectory.isDirectory()) {
            throw new IllegalArgumentException(currentWorkingDirectory.getName() +
                    "is not a directory");
        }
        this.cwd =  currentWorkingDirectory;
        return this;
    }

    /**
     * Run the command written as a List&lt;String&gt;.
     * Wait for the command to complete, then return a
     * Subprocess.CompletedProcess instance, in which
     * you can read:
     * - the return code of the subprocess
     * - the captured STDOUT of the subprocess
     * - the captured STDERR of the subproess
     *
     * Referred to a Baeldung's article <a href="https://www.baeldung.com/java-executor-wait-for-threads">"Java Executor Wait for the Threads"</a>
     *
     * @param command E.g Array.asList("ls", "-la", ".")
     * @return a Subprocess.CompletedProcess instance
     * @throws IOException when failed to create a Thread to consume STDOUT/STDERR from the subprocess
     * @throws InterruptedException when a Thread that consumes STDOUT/STDERR from the subprocess was interrupted
     */
    public CompletedProcess run(List<String> command)
            throws IOException, InterruptedException {
        Objects.requireNonNull(command);
        for (Object arg : command) {
            if (! (arg instanceof String)) {
                throw new IllegalArgumentException(
                        String.format("given argument \"%s\" is not a java.lang.String, is an instance of %s",
                                arg.toString(), arg.getClass().getName())
                );
            }
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(this.cwd);
        builder.command(command);
        Process process = builder.start();
        CompletedProcess cp = new CompletedProcess(command);

        // https://www.baeldung.com/java-executor-wait-for-threads
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        List<Callable<String>> callables =
                Arrays.asList(
                        new StreamGobbler(
                                process.getInputStream(),
                                cp::appendStdout
                        ),
                        new StreamGobbler(
                                process.getErrorStream(),
                                cp::appendStderr
                        )
                );

        // start the threads to consume the stdout and stderr out of the subprocess
        List<Future<String>> futures = threadPool.invokeAll(callables);

        // execute the subprocess
        int returnCode = process.waitFor();

        // wait for the threads to finish
        awaitTerminationAfterShutdown(threadPool);

        // now we are surely done
        cp.setReturnCode(returnCode);
        return cp;
    }

    private static void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A Data Transfer Object that contains the return code, STDOUT and STDERR
     * out of the executed subprocess.
     */
    public static final class CompletedProcess {

        private final List<String> args;
        private int returncode;
        private final List<String> stdout;
        private final List<String> stderr;

        public CompletedProcess(List<String> args) {
            this.args = args;
            this.returncode = -999;
            this.stdout = new ArrayList<>();
            this.stderr = new ArrayList<>();
        }

        void appendStdout(String line) {
            stdout.add(line);
        }

        void appendStderr(String line) {
            stderr.add(line);
        }

        void setReturnCode(int v) {
            this.returncode = v;
        }

        /**
         * @return the return code from the subprocess. 0 indicates normal.
         * other values indicate some error.
         */
        public int returncode() {
            return this.returncode;
        }

        /**
         * @return captured STDOUT from the subprocess.
         */
        public List<String> stdout() {
            return this.stdout;
        }

        /**
         * @return captured STDERR from the subprocess.
         */
        public List<String> stderr() {
            return this.stderr;
        }
    }

    /**
     *
     */
    public static final class StreamGobbler implements Callable<String> {
        private final InputStream inputStream;
        private final Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream,
                             Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public String call() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
            return "done";
        }
    }
}

