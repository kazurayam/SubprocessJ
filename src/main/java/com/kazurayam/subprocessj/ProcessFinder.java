package com.kazurayam.subprocessj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.management.ManagementFactory;
import java.util.stream.Collectors;

import com.kazurayam.subprocessj.Subprocess.CompletedProcess;

public class ProcessFinder {

    ProcessFinder() {}

    /**
     * @return the process id of the OS process in which the current JVM is working
     */
    public static long findCurrentJvmPid() {
        String pname = ManagementFactory.getRuntimeMXBean().getName();
        // System.out.println("process name = " + pname);  // pname may be "4831@KAZUAKInoMacBook-Air-2.local"
        String pid = pname;
        int i = pname.indexOf("@");
        if (i != -1) {
            pid = pname.substring(0, i);
        }
        return Long.parseLong(pid);
    }

    /**
     * Given an IP Port number, identify the id of OS process that is
     * listening to the port. This will use OS-dependent commands, for example
     * `lsof -i:portNumber -P` on Mac and Linux.
     *
     * @param port IP port
     * @return FindingResult includes returncode
     * @throws InterruptedException when the subprosess was interrupted
     * @throws IOException when the subprocess failed
     */
    public static FindingResult findPidByListeningPort(int port)
            throws IOException, InterruptedException
    {
        FindingResult fr = new FindingResult(OSType.getOSType(), port);
        if (fr.ostype() == OSType.MAC || fr.ostype() == OSType.UNIX) {
            // execute "lsof -i:pppp -P" command to find the list of processes
            // which are working of the port
            fr.addAllCommand(Arrays.asList("lsof", "-i:" + String.valueOf(port), "-P"));
            Subprocess sp = new Subprocess();
            CompletedProcess cp = sp.run(fr.command());
            if (cp.returncode() == 0) {
                List<String> filtered =
                        /*
$ lsof -i:80 -P
COMMAND     PID           USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
com.docke   910 kazuakiurayama   91u  IPv6 0xbff554d0cffbd48b      0t0  TCP *:80 (LISTEN)
katalon   12497 kazuakiurayama  147u  IPv6 0xbff554d0cffbab4b      0t0  TCP 192.168.0.8:58990->server-18-65-100-111.kix50.fr.cloudfront.net:80 (ESTABLISHED)
             */
                        cp.stdout().stream()
                                .filter(l ->
                                        l.contains(
                                                String.format(":%d (LISTEN)", port)
                                        ))
                                .collect(Collectors.toList());
                fr.addAllFilteredStdout(filtered);
                if (fr.filteredStdout().size() == 1) {
                    String[] tokens = filtered.get(0).split("\\s+");
                    if (tokens.length >= 1) {
                        try {
                            fr.setProcessId(Long.parseLong(tokens[1]));
                            fr.setReturncode(0);
                        } catch (NumberFormatException e) {
                            fr.setMessage(e.getMessage());
                            fr.setReturncode(-1003);
                        }
                    } else {
                        fr.setMessage("too short tokens");
                        fr.setReturncode(-1002);
                    }
                } else {
                    fr.setMessage(String.format("no process found listening to the IP port:%d", port));
                    fr.setReturncode(-1001);
                }
            } else {
                fr.setMessage("lsof command failed. may be no process is listening to the port " + port);
                fr.addAllStderr(cp.stderr());
                fr.setReturncode(cp.returncode());
            }
        } else if (fr.ostype() == OSType.WINDOWS) {
            fr.setMessage("Windows is yet to be supported");
            fr.setReturncode(-998);
        } else {
            fr.setMessage(String.format("OSType: %s is unsupported", fr.ostype().toString()));
            fr.setReturncode(-999);throw new IllegalStateException("OSType: ${ostype} is unsupported");
        }
        return fr;
    }

    /**
     *
     */
    public static class FindingResult {
        private final OSType ostype;
        private int port = 0;
        private final List<String> command = new ArrayList<>();
        private final List<String> stdout = new ArrayList<>();
        private final List<String> filteredStdout  = new ArrayList<>();
        private final List<String> stderr  = new ArrayList<>();
        private long processId = -1;  // >0 if found, -1 not found
        private int returncode = -1;  // 0: found, non 0: not found
        private String message = "";
        public FindingResult(OSType ostype, int port) {
            this.ostype = ostype;
            this.port = port;
        }
        public OSType ostype() {
            return this.ostype;
        }
        public int port() {
            return this.port;
        }
        public void addAllCommand(List<String> command) {
            this.command.addAll(command);
        }
        public List<String> command() {
            return this.command;
        }
        public void addAllStdout(List<String> stdout) {
            this.stdout.addAll(stdout);
        }
        public List<String> stdout() {
            return this.stdout;
        }
        public void addAllFilteredStdout(List<String> filteredStdout) {
            this.filteredStdout.addAll(filteredStdout);
        }
        public List<String> filteredStdout() {
            return this.filteredStdout;
        }
        public void addAllStderr(List<String> stderr) {
            this.stderr.addAll(stderr);
        }
        public List<String> stderr() {
            return this.stderr;
        }
        public void setProcessId(long processId) {
            this.processId = processId;
        }
        public long processId() {
            return this.processId;
        }
        public void setReturncode(int returncode) {
            this.returncode = returncode;
        }
        public int returncode() {
            return this.returncode;
        }
        public void setMessage(String msg) {
            this.message = msg;
        }
        public String message() {
            return this.message;
        }
    }
}
