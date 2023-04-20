package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLocatorTest {

    /**
     * On Mac, the `git` command will be found at `/usr/local/bin/git`
     */
    @Test
    void test_git_on_Mac() {
        if (OSType.isMac()) {
            CommandLocator.CommandLocatingResult cfr = CommandLocator.find("git");
            assertEquals("/usr/local/bin/git", cfr.command());
            assertEquals(0, cfr.returncode());
        }
    }

    /**
     * `notepad` command should not be there on Mac
     */
    @Test
    void test_pngquant() {
        if (OSType.isMac() || OSType.isUnix()) {
            CommandLocator.CommandLocatingResult cfr = CommandLocator.find("pngquant");
            assertEquals("/usr/local/bin/pngquant", cfr.command());
            assertEquals(0, cfr.returncode());
        } else if (OSType.isWindows()) {
            throw new RuntimeException("TODO");
        }
    }

    /**
     * If "Docker for Windows" is not installed, CL will return rc=-1.
     * If it is installed, still CL will return rc=-2 because "where docker" command will return 2 lines as:
     * <PRE>
     * C:\\Users\\uraya&gt;where docker
     *
     * C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker
     * C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe
     * </PRE>
     */
    @Test
    void test_find_docker_on_Windows() {
        if (OSType.isWindows()) {
            CommandLocator.CommandLocatingResult cfr = CommandLocator.find("docker");
            printCFR("test_find_docker_on_Windows", cfr);
            assertNotEquals(0, cfr.returncode());
        }
    }

    @Test
    void test_find_dockerexe_on_Windows() {
        if (OSType.isWindows()) {
            CommandLocator.CommandLocatingResult cfr = CommandLocator.find("docker.exe");
            printCFR("test_find_dockerexe_on_Windows", cfr);
            assertEquals(0, cfr.returncode());
        }

    }

    /**
     * The returned value depends on the runtime environment.
     *
     * On Mac, this will return
     * <PRE>/usr/local/bin/git</PRE>
     *
     * On Windows, may be
     * <PRE>C:\Program Files\Git\cmd\git.exe</PRE>
     * if the "Git for Windows" is installed.
     *
     * However, if you execute this test in the "Git Bash" shell, there could be 2 git.exe
     * <PRE>
     * C:\Program Files\Git\mingw64\bin\git.exe
     * C:\Program Files\Git\cmd\git.exe
     * </PRE>
     *
     * If "git" is not, it will return rc=-1.
     */
    @Test
    void test_find_git_is_found_startswith_predicate() {
        CommandLocatingResult cfr;
        if (OSType.isWindows()) {
            cfr = CommandLocator.find(
                    "git",
                    CommandLocator.startsWith("C:\\Program Files\\Git\\cmd")
            );
        } else if (OSType.isMac() || OSType.isUnix()) {
            cfr = CommandLocator.find("git");
        } else {
            throw new IllegalStateException(OSType.getOSType() + " is not supported");
        }
        printCFR("test_find_git_is_found_startswith_predicate", cfr);
        assertEquals(0, cfr.returncode());
    }

    @Test
    void test_find_git_is_found_endswith_predicate() {
        CommandLocatingResult cfr;
        if (OSType.isWindows()) {
            cfr = CommandLocator.find(
                    "git",
                    CommandLocator.endsWith("cmd\\git.exe")
            );
        } else if (OSType.isMac() || OSType.isUnix()) {
            cfr = CommandLocator.find("git");
        } else {
            throw new IllegalStateException(OSType.getOSType() + " is not supported");
        }
        printCFR("test_find_git_is_found_endswith_predicate", cfr);
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
            CommandLocator.CommandLocatingResult cfr = CommandLocator.find("date");
            printCFR("test_find_date_on_Windows", cfr);
            assertEquals(0, cfr.returncode());
        }
    }


    private void printCFR(String label, CommandLocatingResult cfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(cfr.toString());
    }
}
