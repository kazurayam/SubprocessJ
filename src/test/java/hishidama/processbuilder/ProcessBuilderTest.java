package hishidama.processbuilder;

import hishidama.Util;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * https://www.ne.jp/asahi/hishidama/home/tech/java/process.html
 */
public class ProcessBuilderTest {

    @Test
    public void test_pb_constructor_start() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        Process process = pb.start();
        Util.printProcessOutput("test_pb_constructor_start", process);
    }

    @Test
    public void test_pb_command_start() throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("java", "-version");
        Process process = pb.start();
        Util.printProcessOutput("test_pb_command_start", process);
    }

    @Test
    public void test_pb_constructor_array_start() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(new String[] {"java", "-version"});
        Process process = pb.start();
        Util.printProcessOutput("test_pb_constructor_array_start", process);
    }

    @Test
    public void test_pb_constructor_list_start() throws IOException {
        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-version");
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();
        Util.printProcessOutput("test_pb_constructor_list_start", process);
    }

    @Test
    public void test_pb_constructor_start_echo_zzz() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("echo", "zzz");
        Process process = pb.start();
        Util.printProcessOutput("test_pb_constructor_start_echo_zzz", process);
    }

    @Test
    public void test_pb_env_vars() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("echo", "$TEST");
        Map<String,String> env = pb.environment();
        env.put("TEST","sample");
        Process process = pb.start();
        Util.printProcessOutput("test_pb_env_vars", process);
    }

    @Test
    public void test_pb_environment() throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        Map<String,String> env = pb.environment();
        for (Map.Entry<String,String> entry : env.entrySet()) {
            System.out.println(String.format("%s=%s", entry.getKey(), entry.getValue()));
        }
    }

    @Test
    public void test_pb_pwd() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("ls", "-la");
        File dir = new File("/Users");
        pb.directory(dir);
        Process process = pb.start();
        Util.printProcessOutput("test_pb_pwd", process);
    }

    @Test
    public void test_pb_merge_stderr_into_stdout() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-version");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        Util.printProcessOutput("test_pb_merge_stderr_into_stdout", process);
    }
}
