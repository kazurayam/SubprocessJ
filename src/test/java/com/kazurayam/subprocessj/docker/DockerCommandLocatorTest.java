package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator;
import com.kazurayam.subprocessj.OSType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DockerCommandFinderTest {

    @Test
    void test_smoke() {
        CommandLocator.CommandLocatingResult cfr = DockerCommandFinder.find();
        assertEquals(0, cfr.returncode());
        String path = cfr.stdout().get(0).trim();
        if (OSType.isMac() || OSType.isUnix()) {
            assertEquals("/usr/local/bin/docker", path);
        } else if (OSType.isWindows()) {
            assertEquals("C:\\Program File\\docke\\docker.exe", path);
        }

    }
}
