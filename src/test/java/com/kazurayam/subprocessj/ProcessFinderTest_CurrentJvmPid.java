package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessFinderTest_CurrentJvmPid {


    @Test
    void test_getCurrentJvmPid() {
        long jvmProcessId = ProcessFinder.findCurrentJvmPid();
        assertTrue(jvmProcessId > 0);
    }

}
