package com.kazurayam.subprocessj.docker.model;

public class ContainerId implements Comparable<ContainerId> {

    public static final ContainerId NULL_OBJECT = new ContainerId("NULL_OBJECT");

    private final String id;

    public ContainerId(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ContainerId)) {
            return false;
        }
        ContainerId other = (ContainerId)obj;
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
    public int compareTo(ContainerId other) {
        return this.id().compareTo(other.id());
    }
}
