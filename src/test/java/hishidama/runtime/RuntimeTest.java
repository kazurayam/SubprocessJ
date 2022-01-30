package hishidama.runtime;

import hishidama.Util;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class RuntimeTest {

    @Test
    public void test_exec_java_version() throws IOException {
        Runtime r = Runtime.getRuntime();
        String[] cmd = new String[] {"java", "-version"};
        Process process = r.exec(cmd);
        Util.printProcessOutput("test_exec_java_version", process);
    }

    @Test
    public void test_exec_echo() throws IOException {
        Runtime r = Runtime.getRuntime();
        String[] cmd = new String[] {"echo","zzz"};
        Process process = r.exec(cmd);
        Util.printProcessOutput("test_exec_echo", process);
    }

    @Test
    public void test_2nd_arg_as_env() throws IOException {
        String[] env = new String[2];
        env[0] = "TEST=sample";
        env[1] = "";

        Runtime r = Runtime.getRuntime();
        Process process = r.exec("sh echo $TEST", env);
        Util.printProcessOutput("test_2nd_arg_as_env", process);
    }

    /**
     * The 3rd argument to Runtime.exec() accepts a File as
     * the current working directory for the process
     *
     * @throws IOException
     */
    @Test
    public void test_3rd_arg_as_cwd() throws IOException {
        File dir = new File("/Users");
        Runtime r = Runtime.getRuntime();
        Process process = r.exec("ls .",null,dir);
        Util.printProcessOutput("test_3rd_arg_as_cwd", process);
    }

}
