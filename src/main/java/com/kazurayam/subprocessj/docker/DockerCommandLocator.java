package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator;
import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.OSType;

/**
 * Find the full file path of "docker" command in the current OS environment.
 * Uses "which" command on Unix-flavord OS; use "where" command on Windows.
 *
 * E.g, on my MacBook, "/usr/local/bin/docker", not "docker"
 * on Windows, it could be something like "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe"
 */
public class DockerCommandLocator {

    public static CommandLocatingResult find() {
        CommandLocatingResult clr;
        if (OSType.isMac() || OSType.isUnix()) {
            clr = CommandLocator.find("docker");
            if (clr.returncode() == 0) {
                return clr;
            } else {
                return CommandLocator.find("/usr/local/bin/docker");
            }
        } else if (OSType.isWindows()) {
            clr = CommandLocator.find("docker.exe");
            if (clr.returncode() == 0) {
                return clr;
            } else {
                return CommandLocator.find(
                        "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe");
            }
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
