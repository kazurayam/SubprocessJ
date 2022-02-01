package com.kazurayam.subprocessj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.kazurayam.subprocessj.ProcessFinder.FindingResult;
import com.kazurayam.subprocessj.ProcessTerminator.TerminationResult;

public class TerminationResultTest {

    TerminationResult tr;

    @BeforeEach
    public void setup() {
        FindingResult fr = new FindingResult(OSType.getOSType(), 8500);
        tr = new TerminationResult(fr);
    }

    @Test
    public void test_constructor() {
        tr.getFindingResult().ifPresent( fr -> {
            assertEquals(8500, fr.port());
            assertTrue(fr.ostype() == OSType.MAC || fr.ostype() == OSType.WINDOWS);
            assertEquals(-999, fr.returncode());
            assertEquals(0, fr.stdout().size());
            assertEquals(0, fr.stderr().size());
            assertEquals(0, fr.filteredStdout().size());
            assertEquals(-999L, fr.processId());
        });
    }

    @Test
    public void test_setReturncode() {
        tr.setReturncode(0);
        assertEquals(0, tr.returncode());
    }

    @Test
    public void test_setMessage() {
        tr.setMessage("hello");
        assertEquals("hello", tr.message());
    }

}
