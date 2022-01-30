package hishidama.process;

import hishidama.StreamGobbler;
import hishidama.Util;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProcessTest {

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
        StreamGobbler outGobbler = new StreamGobbler(process.getInputStream());
        outGobbler.start();

        StreamGobbler errGobbler = new StreamGobbler(process.getErrorStream());
        errGobbler.start();

        // wait for the subprocess to finish
        process.waitFor();

        // wait for the threads to finshi consuming the outputs
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
}
