package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * In the command line, you can do this
 * ```
 * $ echo $PATH
 * /usr/local/bin:usr/local/sbin:/Users/kazurayam/.nodebrew/current/bin:/Users/kazurayam.pyenv/shims:...
 * ```
 *
 * How to the same from Java?
 * You can do it using the Subprocess#environment()
 */
public class EchoPathTest {

    @Test
    public void test_get_environment_values_as_Map() {
        Subprocess sp = new Subprocess();
        Map<String, String> env = sp.environment();
        System.out.println(String.format("PATH: %s", env.get("PATH")));
        // split the PATH value by ":", print the elements by line
        List<String> values = Arrays.asList(env.get("PATH").split(":"));
        values.stream().sorted().forEach(System.out::println);
    }

    @Test
    public void test_get_environment_variable_value() {
        Subprocess sp = new Subprocess();
        String pathValue = sp.environment("PATH");
        System.out.println(String.format("PATH: %s", pathValue));
        assertNotNull(pathValue);
    }
}
