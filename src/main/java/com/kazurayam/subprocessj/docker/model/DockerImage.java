package com.kazurayam.subprocessj.docker.model;

public class DockerImage implements Comparable<DockerImage> {
    public static final DockerImage NULL_OBJECT = new DockerImage("NULL_OBJECT");
    private final String id;
    public DockerImage(String id) {
        this.id = id;
    }
    public String id() {
        return this.id;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DockerImage)) {
            return false;
        }
        DockerImage other = (DockerImage)obj;
        return this.id().equals(other.id());
    }
    @Override
    public int hashCode() {
        return this.id().hashCode();
    }
    @Override
    public String toString() {
        return this.id();
    }
    @Override
    public int compareTo(DockerImage other) {
        return this.id().compareTo(other.id());
    }
}