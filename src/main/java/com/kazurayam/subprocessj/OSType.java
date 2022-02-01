package com.kazurayam.subprocessj;

public enum OSType {

    WINDOWS,
    MAC,
    UNIX,
    SOLARIS,
    UNKNOWN,
    ;

    public static OSType getOSType() {
        return getOSType(System.getProperty("os.name").toLowerCase());
    }

    public static OSType getOSType(String os) {
        if (os.contains("win")) {
            return WINDOWS;
        } else if (os.contains("mac")) {
            return MAC;
        } else if (os.contains("nix")
                || os.contains("nux")
                || os.contains("aix")) {
            return UNIX;
        } else if (os.contains("sunos")) {
            return SOLARIS;
        } else {
            return UNKNOWN;
        }
    }

    public static boolean isWindows() {
        return getOSType() == WINDOWS;
    }

    public static boolean isMac() {
        return getOSType() == MAC;
    }

    public static boolean isUnix() {
        return getOSType() == UNIX;
    }

}
