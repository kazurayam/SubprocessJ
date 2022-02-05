package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.OSType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DockerCommandLocatorTest {

    @Test
    void test_smoke() {
        CommandLocatingResult clr = DockerCommandLocator.find();
        assertEquals(0, clr.returncode(), "You do not have docker installed.\n" + clr.toString());
        String path = clr.stdout().get(0).trim();
        if (OSType.isMac() || OSType.isUnix()) {
            assertEquals("/usr/local/bin/docker", path);
        } else if (OSType.isWindows()) {
            assertEquals("C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker", path);
        }

    }
}
