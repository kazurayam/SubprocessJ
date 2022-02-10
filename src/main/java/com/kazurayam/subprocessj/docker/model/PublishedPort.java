package com.kazurayam.subprocessj.docker.model;

public class PublishedPort {

    public static final PublishedPort NULL_OBJECT = new PublishedPort(0,0);

    private final int containerPort;
    private final int hostPort;
    private final Protocol protocol;

    public PublishedPort(int hostPort, int containerPort) {
        this(hostPort, containerPort, Protocol.tcp);
    }

    public PublishedPort(int hostPort, int containerPort, Protocol protocol) {
        this.hostPort = hostPort;
        this.containerPort = containerPort;
        this.protocol = protocol;
    }

    public int hostPort() {
        return this.hostPort;
    }

    public int containerPort() {
        return this.containerPort;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    /*
     * 0.0.0.0:80->8080/tcp
     */
    @Override
    public String toString() {
        return "0.0.0.0:" + this.hostPort() + "->" +
                this.containerPort() + "/" + this.protocol();
    }
}
