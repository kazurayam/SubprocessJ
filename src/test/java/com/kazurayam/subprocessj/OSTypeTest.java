package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OSTypeTest {

    /**
     * Which OS am I working on now?
     */
    @Test
    void test_getOSType() {
        assertTrue(OSType.isMac() || OSType.isUnix() || OSType.isWindows());
    }
}
