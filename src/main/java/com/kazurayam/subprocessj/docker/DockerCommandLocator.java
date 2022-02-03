package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator;
import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;

/**
 * Find the full file path of "docker" command in the current OS environment.
 * Uses "which" command on Unix-flavord OS; use "where" command on Windows.
 *
 * E.g, on my MacBook, "/usr/local/bin/docker", not "docker"
 * on Windows, it could be something like "C:\\Program File\\docke\\docker.exe"
 */
public class DockerCommandLocator {

    public static CommandLocatingResult find() {
        return CommandLocator.find("docker");
    }

    public static CommandLocatingResult where() {
        return find();
    }

    public static CommandLocatingResult which() {
        return find();
    }

}
