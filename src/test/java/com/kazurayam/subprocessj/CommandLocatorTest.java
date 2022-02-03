package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CommandLocatorTest {

    /**
     * The returned value depends on the runtime environment.
     *
     * On Mac, this will return
     * <PRE>/usr/local/bin/git</PRE>
     *
     * On Windows, may be if the "Git for Windows" is installed.
     * If not, it will return rc=-1.
     */
    @Test
    void test_find_git_is_found() {
        CommandLocator.CommandLocatingResult cfr = CommandLocator.find("git");
        printCFR("test_find_git_is_found", cfr);
        assertEquals(0, cfr.returncode());
    }

    /**
     * just an alias to find(String command)
     */
    @Test
    void test_which_git() {
        CommandLocatingResult cfr = CommandLocator.which("git");
        printCFR("test_which_git", cfr);
        assertEquals(0, cfr.returncode());
    }

    /**
     * one more alias to find(String command)
     */
    @Test
    void test_where_git() {
        CommandLocatingResult cfr = CommandLocator.where("git");
        printCFR("test_where_git", cfr);
        assertEquals(0, cfr.returncode());
    }

    /**
     * The "tiger" command is expected NOT to be there
     */
    @Test
    void test_find_tiger_not_exists() {
        CommandLocator.CommandLocatingResult cfr = CommandLocator.find("tiger");
        printCFR("test_find_tiger_not_exists", cfr);
        assertNotEquals(0, cfr.returncode());
    }

    /**
     * On Windows, the "date" command is implemented as a sub-command of cmd.exe.
     * So CommandFinder.find("date") will return non-zero, no Path found.
     *
     * NO.
     * If you have Git Bash installed, you will have "C:\\Program Files\\Git\\usr\\bin\\date.exe
     */
    @Disabled
    @Test
    void test_find_date_on_Windows() {
        if (OSType.isWindows()) {
            CommandLocator.CommandLocatingResult cfr = CommandLocator.where("date");
            printCFR("test_find_date_on_Windows", cfr);
            assertEquals(0, cfr.returncode());
        }
    }

    private void printCFR(String label, CommandLocatingResult cfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(cfr.toString());
    }
}
