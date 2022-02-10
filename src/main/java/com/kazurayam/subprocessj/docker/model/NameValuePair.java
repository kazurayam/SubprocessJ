package com.kazurayam.subprocessj.docker.model;

import java.util.Objects;

/**
 * stands for a pair of <PRE>name=value</PRE> pair as an optional argument to a command
 */
public class NameValuePair {

    private final String name;
    private final String value;

    public NameValuePair(String name) {
        this(name, null);
    }

    /**
     *
     * @param name name of a variable required, not null-able
     * @param value a String; can be null
     */
    public NameValuePair(String name, String value) {
        Objects.requireNonNull(name);
        this.name = name;
        this.value = value;
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name.contains(" ") || value.contains(" ")) {
            sb.append("\"");
            sb.append(name);
            sb.append("=");
            sb.append(value);
            sb.append("\"");
        } else {
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }
        return sb.toString();
    }
}
