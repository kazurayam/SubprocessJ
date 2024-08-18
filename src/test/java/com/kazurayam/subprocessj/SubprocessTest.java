package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class SubprocessTest {

    @Test
    void test_list() throws Exception {
        Subprocess.CompletedProcess cp;
        if (OSType.isMac() || OSType.isUnix()) {
            cp = new Subprocess().cwd(new File("."))
                    .run(Arrays.asList("sh", "-c", "ls")
                    );
        } else {
            cp = new Subprocess().cwd(new File("."))
                    .run(Arrays.asList("cmd.exe", "/C", "dir")
                    );
        }
        assertEquals(0, cp.returncode());
        assertTrue(cp.stdout().size() > 0);
        cp.stdout().forEach(System.out::println);
        cp.stderr().forEach(System.err::println);
        assertTrue(cp.stdout().toString().contains("src"));
    }

    @Test
    void test_date() throws Exception {
        Subprocess.CompletedProcess cp;
        if (OSType.isMac() || OSType.isUnix()) {
            cp = new Subprocess().run(Arrays.asList("/bin/date"));
        } else {
            // I could not find out how to execute "date" command on Windows.
            cp = new Subprocess().run(Arrays.asList("java", "-version"));
        }
        assertEquals(0, cp.returncode());
        cp.stdout().forEach(System.out::println);
        cp.stderr().forEach(System.err::println);
        assertTrue(cp.stdout().size() > 0 || cp.stderr().size() > 0);
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

    @Test
    void test_environment_readOnly() {
        Subprocess sp = new Subprocess();
        Map<String, String> env = sp.environment();
        assertNotNull(env);
        assertNotNull(env.get("PATH"));
        /*
        env.keySet().forEach(key -> {
            String value = env.get(key);
            System.out.println(String.format("%s: %s", key, value));
        });
        */
    }

    /**
     * See https://github.com/kazurayam/VBACallGraph/issues/45 for the background info
     */
    @Test
    void test_environment_setValue() {
        Subprocess sp = new Subprocess();
        Map<String, String> env = sp.environment();
        env.put("PLANTUML_LIMIT_SIZE", "8192");
        //
        String actual = sp.environment("PLANTUML_LIMIT_SIZE");
        assertEquals("8192", actual);
    }
}
