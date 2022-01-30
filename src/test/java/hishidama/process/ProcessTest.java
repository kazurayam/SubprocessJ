package hishidama.process;

import hishidama.InputStreamThread;
import hishidama.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessTest {

    static Path projectDir;
    static Path classOutputDir;

    @BeforeAll
    public static void beforeAll() throws IOException {
        projectDir = Paths.get(".");
        classOutputDir = projectDir.resolve("./build/tmp/testOutput/ProcessTest");
        if (Files.exists(classOutputDir)) {
            Files.walk(classOutputDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void test_process_waitFor() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        Process process = pb.start();
        int ret = process.waitFor();
        System.out.println("ret=" + ret);
    }

    @Test
    public void test_process_exitValue() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        Process process = pb.start();
        process.waitFor();
        int ret = process.exitValue();
        System.out.println("ret=" + ret);
    }

    @Test
    public void test_process_print_output() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        Process process = pb.start();
        process.waitFor();
        Util.printProcessOutput("test_process_print_output", process);
    }

    @Test
    public void test_process_print_large_output_hangs() throws IOException, InterruptedException {
        File tempfile = File.createTempFile("geeks", null);
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempfile));
        for (int i = 0; i <= 1024; i++) {
            bw.write('.');
        }
        bw.flush();
        bw.close();
        //
        List<String> args = new ArrayList<>();
        args.add("cat");
        args.add(tempfile.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();
        process.waitFor();
        Util.printProcessOutput("test_process_print_large_output_hangs", process);
    }

    @Test
    public void test_gobbling_output() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", "./build/classes/java/test", "hishidama.Shouter");
        Process process = pb.start();

        // start threads to consume output from the subprocess
        InputStreamThread outGobbler = new InputStreamThread(process.getInputStream());
        outGobbler.start();

        InputStreamThread errGobbler = new InputStreamThread(process.getErrorStream());
        errGobbler.start();

        // wait for the subprocess to finish
        process.waitFor();

        // wait for the threads to finish consuming the outputs
        outGobbler.join();
        errGobbler.join();

        System.out.println("ret=" + process.exitValue());

        // pipe STDOUT from the subprocess
        System.out.println("[STDOUT]");
        for (String s : outGobbler.getStringList()) {
            System.out.println(s);
        }
        System.out.println("[/STDOUT]");

        // pipe STDERR from the subprocess
        System.err.println("[STDERR]");
        for (String s : errGobbler.getStringList()) {
            System.err.println(s);
        }
        System.err.println("[/STDERR]");
    }

    /**
     * When you can not use Thread in your application,
     * then the StreamGobbler does not help. In this case,
     * redirecting STDOUT and STDERR from subprocess
     * into files by SHELL operators `>` and `2>` may help.
     */
    @Test
    void test_redirect() throws IOException, InterruptedException {
        Path methodOutputDir = classOutputDir.resolve("test_redirect");
        Files.createDirectories(methodOutputDir);
        Path script = methodOutputDir.resolve("script.sh").normalize().toAbsolutePath();
        Util.writeLine("javac -help", script);
        Path stdoutFile = methodOutputDir.resolve("stdout.txt").normalize().toAbsolutePath();
        Path stderrFile = methodOutputDir.resolve("stderr.txt").normalize().toAbsolutePath();
        System.out.println(stdoutFile.toString());
        System.err.println(stderrFile.toString());
        List<String> args = Arrays.asList("/bin/sh",
                script.toString(),
                ">", stdoutFile.toString(),
                "2>", stderrFile.toString()
        );
        System.out.println(args);
        //
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();

        //Util.printProcessOutput("test_redirect", process);

        // no need to consume stdout/stderr from the process

        int ret = process.waitFor();
        System.out.println("ret=" + ret);
    }

    @Test
    void test_timeout() throws IOException, InterruptedException {
        // startup com.kazurayam.subprocessj.HiThereServer
        List<String> args = Arrays.asList(
                "java",
                "-cp", "build/classes/java/main",
                "com.kazurayam.subprocessj.HiThereServer"
        );
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();
        //
        boolean end = process.waitFor(5, TimeUnit.SECONDS);
        //
        Util.printProcessOutput("test_timeout", process);
        if (end) {
            System.out.println("finished by timeout with ret=" + process.exitValue());
        } else {
            System.err.println("finished by timeout");
        }
        process.destroy();
    }
}
