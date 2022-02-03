package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.docker.model.ContainerId;
import com.kazurayam.subprocessj.docker.model.DockerImage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.kazurayam.subprocessj.docker.ContainerFinder.ContainerFindingResult;
import com.kazurayam.subprocessj.docker.ContainerRunner.ContainerRunningResult;
import com.kazurayam.subprocessj.docker.ContainerStopper.ContainerStoppingResult;
import com.kazurayam.subprocessj.docker.model.PublishedPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContainerStartFindStopTest {

    private static final int HOST_PORT = 3080;

    private static final PublishedPort publishedPort = new PublishedPort(HOST_PORT, 8080);
    private static final DockerImage image = new DockerImage("kazurayam/flaskr-kazurayam:1.1.0");

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        File directory = Files.createTempDirectory("ContainerFinderTest").toFile();
        ContainerRunningResult crr =
                ContainerRunner.runContainerAtHostPort(directory, publishedPort, image);
        if (crr.returncode() != 0) {
            throw new IllegalStateException(crr.toString());
        }
    }

    @AfterAll
    public static void afterAll() throws IOException, InterruptedException {
        ContainerFindingResult cfr = ContainerFinder.findContainerByHostPort(HOST_PORT);
        if (cfr.returncode() == 0) {
            ContainerId containerId = cfr.containerId();
            ContainerStoppingResult csr = ContainerStopper.stopContainer(containerId);
            if (csr.returncode() != 0) {
                throw new IllegalStateException(csr.toString());
            }
        } else {
            throw new IllegalStateException(cfr.toString());
        }
    }

    @Test
    public void test_findByHostPort_found() throws IOException, InterruptedException {
        ContainerFindingResult cfr = ContainerFinder.findContainerByHostPort(HOST_PORT);
        printResult("test_findingByHostPort_found", cfr);
        assertEquals(0, cfr.returncode());
    }

    @Test
    public void test_findByHostPort_notFound() throws IOException, InterruptedException {
        ContainerFindingResult cfr = ContainerFinder.findContainerByHostPort(HOST_PORT + 1);
        printResult("test_findingByHostPort_notFound", cfr);
        assertNotEquals(0, cfr.returncode());
    }

    private void printResult(String label, ContainerFindingResult cfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(cfr.toString());
    }
}
