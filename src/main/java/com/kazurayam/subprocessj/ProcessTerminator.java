package com.kazurayam.subprocessj;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
import com.kazurayam.subprocessj.ProcessFinder.FindingResult;

/**
 * killProcessListeningPort(int portNumber) identifies the running process
 * that is listening the IP port #portNumber, get the Process ID,
 * then kill the process.
 *
 * I want this class to run on Java 8. Therefore I can not rely on the
 * `long pid()` method of `java.lang.Process` which was added at Java 9.
 *
 * So I will use `lsof -i:portNumber -P` command on Mac
 *
 * on Windows? TODO later
 *
 */
public class ProcessTerminator {

    public ProcessTerminator() {}

    /**
     *
     * @param portNumber the IP port number on which the Process is hanging on; you want to kill that process.
     * @return the ID of the process (&gt;0) which is listening to the IP portNumber
     * and therefore killed; returns -1 if the process was not found.
     * @throws IOException if failed to kill a OS process
     * @throws InterruptedException if the process was interrupted
     */
    public static TerminationResult killProcessOnPort(int portNumber)
            throws IOException, InterruptedException
    {
        FindingResult fr =
                ProcessFinder.findPidByListeningPort(portNumber);

        TerminationResult tr;
        if (fr.returncode() == 0) {
            long currentPid = ProcessFinder.findCurrentJvmPid();
            if (fr.processId() == currentPid) {
                // we should NEVER kill ourself
                tr = new TerminationResult();
                tr.setMessage("we should not kill ourself");
                tr.setReturncode(-899);
            } else {
                tr = killProcessByPid(fr);
                tr.setReturncode(0);
            }
        } else {
            tr = new TerminationResult();
            tr.setMessage("there is no process listening to the port " + portNumber);
            tr.setReturncode(-898);
        }
        return tr;
    }

    public static TerminationResult killProcessByPid(FindingResult rs)
            throws IOException, InterruptedException
    {
        TerminationResult tr = new TerminationResult(rs);
        if (rs.processId() == ProcessFinder.findCurrentJvmPid()) {
            tr.setMessage("you should never kill the current process");
            tr.setReturncode(-899);
        } else {
            Subprocess subprocess = new Subprocess();
            if (OSType.isMac() || OSType.isUnix() || OSType.isWindows()) {
                CompletedProcess cp;
                if (OSType.isWindows()) {
                    cp = subprocess.run(
                            Arrays.asList("taskkill", "/f", "/pid", String.valueOf(rs.processId()))
                    );
                } else {
                    cp = subprocess.run(
                            Arrays.asList("kill", String.valueOf(rs.processId()))
                    );
                }
                if (cp.returncode() == 0) {
                    tr.setReturncode(0);
                } else {
                    tr.setMessage(cp.stderr().toString());
                    tr.setReturncode(cp.returncode());
                }
            } else {
                tr.setReturncode(-897);
                tr.setMessage("unsupported OS type");
            }

        }
        return tr;
    }


    /**
     * A Data Transfer Object that contains the return code,
     * STDOUT of `lsof -i:port -P` command that reveals how ProcessKiller worked.
     */
    public static class TerminationResult {

        private final FindingResult findingResult;
        private String message;
        private int returncode;

        public TerminationResult() {
            this.message = "";
            this.findingResult = null;
        }

        public TerminationResult(FindingResult result) {
            this.findingResult = null;
        }

        public Optional<FindingResult> getFindingResult() {
            if (findingResult != null) {
                return Optional.of(findingResult);
            } else {
                return Optional.empty();
            }
        }

        public void setMessage(String msg) {
            this.message = msg;
        }

        public String message() {
            return this.message;
        }

        public void setReturncode(int returncode) {
            this.returncode = returncode;
        }

        public int returncode() {
            return this.returncode;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8500;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        FindingResult fr = ProcessFinder.findPidByListeningPort(port);
        if (fr.returncode() == 0) {
            TerminationResult tr = ProcessTerminator.killProcessOnPort(8500);
            if (tr.returncode() != 0) {
                System.out.println("failed to terminate pid=" + fr.processId());
            }
        } else {
            System.err.println("no process is listening to the port " + port);
        }
    }
}

