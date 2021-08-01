package com.kazurayam.subprocessj;

import java.util.ArrayList;
import java.util.List;

/**
 * A Data Transfer Object that contains the return code, STDOUT and STDERR
 * out of the executed subprocess.
 */
public class CompletedProcess {

    private final List<String> args;
    private int returncode;
    private final List<String> stdout;
    private final List<String> stderr;

    public CompletedProcess(List<String> args) {
        this.args = args;
        this.returncode = -999;
        this.stdout = new ArrayList<String>();
        this.stderr = new ArrayList<String>();
    }

    protected void appendStdout(String line) {
        stdout.add(line);
    }

    protected void appendStderr(String line) {
        stderr.add(line);
    }

    protected void setReturnCode(int v) {
        this.returncode = v;
    }

    /**
     * @return the return code from the subprocess. 0 indicates normal.
     * other values indicate some error.
     */
    public int returncode() {
        return this.returncode;
    }

    /**
     * @return captured STDOUT from the subprocess.
     */
    public List<String> stdout() {
        return this.stdout;
    }

    /**
     * @return captured STDERR from the subprocess.
     */
    public List<String> stderr() {
        return this.stderr;
    }
}
