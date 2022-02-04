package com.kazurayam.subprocessj.docker.model;

/**
 * Represents a single line of "docker ps" command output.
 *
 * <PRE>
 * $ docker ps
 * CONTAINER ID   IMAGE                              COMMAND                  CREATED         STATUS         PORTS                  NAMES
 * d4d4a795d76d   kazurayam/flaskr-kazurayam:1.1.0   "waitress-serve --po…"   9 seconds ago   Up 8 seconds   0.0.0.0:80-&gt;8080/tcp   serene_cannon
 * </PRE>
 *
 * At the moment, I am interested only 3 items: container-id, port and image.
 *
 * https://docs.docker.com/engine/reference/commandline/run/
 */
public class ContainerListEntity implements Comparable<ContainerListEntity> {

    private final ContainerId containerId;
    private final PublishedPort publishedPort;
    private final DockerImage dockerImage;

    public ContainerListEntity(ContainerId containerId,
                               PublishedPort publishedPort,
                               DockerImage dockerImage) {
        this.containerId = containerId;
        this.publishedPort = publishedPort;
        this.dockerImage = dockerImage;
    }
    public ContainerId containerId() {
        return this.containerId;
    }
    public PublishedPort publishedPort() {
        return this.publishedPort;
    }
    public DockerImage dockerImage() {
        return this.dockerImage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"containerId\":\"" + this.containerId().id() + "\"");
        sb.append(",");
        sb.append("\"publishedPort\":\"" + this.publishedPort().toString() + "\"");
        sb.append(",");
        sb.append("\"dockerImage\":\"" + this.dockerImage().id() + "\"");
        sb.append("}");
        return sb.toString();
    }
    @Override
    public int compareTo(ContainerListEntity other) {
        return this.containerId().compareTo(other.containerId());
    }

}
