package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.docker.model.ContainerId;
import com.kazurayam.subprocessj.docker.model.ContainerListEntity;
import com.kazurayam.subprocessj.docker.model.DockerImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.kazurayam.subprocessj.docker.model.PublishedPort;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ContainerListEntityTest {

    private ContainerListEntity entry;

    @BeforeEach
    public void setup() {
        ContainerId containerId = new ContainerId("d4d4a795d76d");
        PublishedPort publishedPort = new PublishedPort(80, 8080);
        DockerImage dockerImage = new DockerImage("kazurayam/flaskr-kazurayam:1.1.0");
        entry = new ContainerListEntity(containerId, publishedPort, dockerImage);
    }

    @Test
    public void test_smoke() {
        assertNotNull(entry);
    }

    @Test
    public void test_toString() {
        String s = entry.toString();
        System.out.println(s);
    }
}
