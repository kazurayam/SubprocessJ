package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.OSType;
import com.kazurayam.subprocessj.Subprocess;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DockerCommandLocatorTest {

    @Test
    void test_find_docker_on_Mac() {
        if (OSType.isMac()) {
            CommandLocatingResult clr = DockerCommandLocator.find();
            assertEquals(0, clr.returncode(), "You do not have docker installed.\n" + clr.toString());
            String path = clr.stdout().get(0).trim();
            assertEquals("/usr/local/bin/docker", path);
            // try to run "docker --help" command which was found above
            String dockerCommand = clr.stdout().get(0);
            try {
                CompletedProcess cp = new Subprocess().run(Arrays.asList(dockerCommand, "--help"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

    @Test
    void test_find_docker_on_Windows() {
        if (OSType.isWindows()) {
            CommandLocatingResult clr = DockerCommandLocator.find();
            assertEquals(0, clr.returncode(), "You do not have docker installed.\n" + clr.toString());
            String path = clr.stdout().get(0).trim();
            assertEquals("C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe", path);
            // try to run "docker --help" command which was found above
            String dockerCommand = clr.stdout().get(0);
            try {
                CompletedProcess cp = new Subprocess().run(Arrays.asList(dockerCommand, "--help"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }



}
