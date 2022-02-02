package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;
import com.kazurayam.subprocessj.CommandFinder.CommandFindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CommandFinderTest {

    @Test
    void test_find_git_is_found() {
        CommandFindingResult cfr = CommandFinder.find("git");
        printCFR("test_find_git_is_found", cfr);
        assertEquals(0, cfr.returncode());
    }

    @Test
    void test_which_git() {
        CommandFindingResult cfr = CommandFinder.which("git");
        printCFR("test_which_git", cfr);
        assertEquals(0, cfr.returncode());
    }

    @Test
    void test_where_git() {
        CommandFindingResult cfr = CommandFinder.where("git");
        printCFR("test_where_git", cfr);
        assertEquals(0, cfr.returncode());
    }

    /**
     * The "tig" command is expected not to be there
     */
    @Test
    void test_find_tig_not_exists() {
        CommandFindingResult cfr = CommandFinder.find("tig");
        printCFR("test_find_tig_not_exists", cfr);
        assertNotEquals(0, cfr.returncode());
    }

    /**
     * On Windows, the "date" command is implemented as a sub-command of cmd.exe.
     * So CommandFinder.find("date") will return non-zero, no Path found.
     */
    @Test
    void test_find_date_on_Windows() {
        if (OSType.isWindows()) {
            CommandFindingResult cfr = CommandFinder.where("date");
            printCFR("test_find_date_on_Windows", cfr);
            assertNotEquals(0, cfr.returncode());
        }
    }

    private void printCFR(String label, CommandFindingResult cfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(cfr.toString());
    }
}
