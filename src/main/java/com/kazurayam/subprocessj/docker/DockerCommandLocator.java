package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator;
import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.OSType;

/**
 * Find the full file path of "docker" command in the current OS environment.
 * Uses "which" command on Unix-flavord OS; use "where" command on Windows.
 *
 * E.g, on my MacBook, "/usr/local/bin/docker", not "docker"
 * on Windows, it could be something like "C:\\Program File\\docke\\docker.exe"
 */
public class DockerCommandLocator {

    public static CommandLocatingResult find() {
        if (OSType.isMac() || OSType.isUnix()) {
            return CommandLocator.find("docker");
        } else if (OSType.isWindows()) {
            return CommandLocator.find("docker.exe");
        } else {
            throw new IllegalStateException("OSType." + OSType.getOSType() + " is not supported");
        }
    }

    public static CommandLocatingResult where() {
        return find();
    }

    public static CommandLocatingResult which() {
        return find();
    }

}
