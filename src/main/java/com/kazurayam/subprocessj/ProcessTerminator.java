package com.kazurayam.subprocessj;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;


import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
import com.kazurayam.subprocessj.ProcessFinder.ProcessFindingResult;

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
    public static ProcessTerminationResult killProcessOnPort(int portNumber)
            throws IOException, InterruptedException
    {
        ProcessFindingResult pfr =
                ProcessFinder.findPidByListeningPort(portNumber);

        ProcessTerminationResult ptr;
        if (pfr.returncode() == 0) {
            long currentPid = ProcessFinder.findCurrentJvmPid();
            if (pfr.processId() == currentPid) {
                // we should NEVER kill ourself
                ptr = new ProcessTerminationResult();
                ptr.setMessage("we should not kill ourself");
                ptr.setReturncode(-899);
            } else {
                ptr = killProcessByPid(pfr);
                ptr.setReturncode(0);
            }
        } else {
            ptr = new ProcessTerminationResult();
            ptr.setMessage("there is no process listening to the port " + portNumber);
            ptr.setReturncode(-898);
        }
        return ptr;
    }

    public static ProcessTerminationResult killProcessByPid(ProcessFindingResult pfr)
            throws IOException, InterruptedException
    {
        ProcessTerminationResult ptr = new ProcessTerminationResult(pfr);
        if (pfr.processId() == ProcessFinder.findCurrentJvmPid()) {
            ptr.setMessage("you should never kill the current process");
            ptr.setReturncode(-899);
        } else {
            Subprocess subprocess = new Subprocess();
            if (OSType.isMac() || OSType.isUnix() || OSType.isWindows()) {
                CompletedProcess cp;
                if (OSType.isWindows()) {
                    cp = subprocess.run(
                            Arrays.asList("taskkill", "/f", "/pid", String.valueOf(pfr.processId()))
                    );
                } else {
                    cp = subprocess.run(
                            Arrays.asList("kill", String.valueOf(pfr.processId()))
                    );
                }
                if (cp.returncode() == 0) {
                    ptr.setReturncode(0);
                } else {
                    ptr.setMessage(cp.stderr().toString());
                    ptr.setReturncode(cp.returncode());
                }
            } else {
                ptr.setReturncode(-897);
                ptr.setMessage("unsupported OS type");
            }

        }
        return ptr;
    }


    /**
     * A Data Transfer Object that contains the return code,
     * STDOUT of `lsof -i:port -P` command that reveals how ProcessKiller worked.
     */
    public static class ProcessTerminationResult {

        private ProcessFindingResult pfr = null;
        private String message = "";
        private int returncode = -1;

        public ProcessTerminationResult() {}

        public ProcessTerminationResult(ProcessFindingResult result) {
            this.pfr = result;
        }

        public Optional<ProcessFindingResult> getProcessFindingResult() {
            if (pfr != null) {
                return Optional.of(pfr);
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

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(new BufferedWriter(sw));
            pw.println("<ptr rc=\"" + this.returncode() + "\">");
            pw.println("<message>" + this.message() + "</message>");
            this.getProcessFindingResult().ifPresent(pw::print);
            pw.println("</ptr>");
            pw.flush();
            pw.close();
            return sw.toString();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 8500;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ProcessFindingResult fr = ProcessFinder.findPidByListeningPort(port);
        if (fr.returncode() == 0) {
            ProcessTerminationResult tr = ProcessTerminator.killProcessOnPort(8500);
            if (tr.returncode() != 0) {
                System.out.println("failed to terminate pid=" + fr.processId());
            }
        } else {
            System.err.println("no process is listening to the port " + port);
        }
    }
}

