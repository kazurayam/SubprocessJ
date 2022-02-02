package com.kazurayam.subprocessj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     */
    public static ProcessFindingResult findPidByListeningPort(int port)
            throws IOException, InterruptedException
    {
        ProcessFindingResult pfr = new ProcessFindingResult(OSType.getOSType(), port);
        if (pfr.ostype() == OSType.MAC || pfr.ostype() == OSType.UNIX) {
            // execute "lsof -i:pppp -P" command to find the list of processes
            // which are working of the port
            pfr.addAllCommand(Arrays.asList("lsof", "-i:" + String.valueOf(port), "-P"));
            Subprocess sp = new Subprocess();
            CompletedProcess cp = sp.run(pfr.command());
            if (cp.returncode() == 0) {
                List<String> filtered =
                        /*
$ lsof -i:80 -P
COMMAND     PID           USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
com.docke   910 kazuakiurayama   91u  IPv6 0xbff554d0cffbd48b      0t0  TCP *:80 (LISTEN)
katalon   12497 kazuakiurayama  147u  IPv6 0xbff554d0cffbab4b      0t0  TCP 192.168.0.8:58990->server-18-65-100-111.kix50.pfr.cloudfront.net:80 (ESTABLISHED)
             */
                        cp.stdout().stream()
                                .filter(l ->
                                        l.contains(
                                                String.format(":%d (LISTEN)", port)
                                        ))
                                .collect(Collectors.toList());
                pfr.addAllFilteredStdout(filtered);
                if (pfr.filteredStdout().size() == 1) {
                    String[] tokens = filtered.get(0).split("\\s+");
                    if (tokens.length >= 1) {
                        try {
                            pfr.setProcessId(Long.parseLong(tokens[1]));
                            pfr.setReturncode(0);
                        } catch (NumberFormatException e) {
                            pfr.setMessage(e.getMessage());
                            pfr.setReturncode(-1003);
                        }
                    } else {
                        pfr.setMessage("too short tokens");
                        pfr.setReturncode(-1002);
                    }
                } else {
                    pfr.setMessage(String.format("no process found listening to the IP port:%d", port));
                    pfr.setReturncode(-1001);
                }
            } else {
                pfr.setMessage("lsof command failed. may be no process is listening to the port " + port);
                pfr.addAllStderr(cp.stderr());
                pfr.setReturncode(cp.returncode());
            }
        } else if (pfr.ostype() == OSType.WINDOWS) {
            pfr.addAllCommand(Arrays.asList("netstat", "-ano"));
            Subprocess sp = new Subprocess();
            CompletedProcess cp = sp.run(pfr.command());
            if (cp.returncode() == 0) {
                List<String> filtered =
            /*
            $ netstat -ano | find "LISTEN" | find "80"
  TCP         0.0.0.0:13688          0.0.0.0:0              LISTENING       4080
  TCP         [::]:13688             [::]:0                 LISTENING       4080
             */
                        // protocol   local-address  exteria-address  state  process-id
                        cp.stdout().stream()
                                .filter(l ->
                                        l.contains("LISTENING") &&
                                        l.matches(makeRegexForFilteringWindowsNetstatOutput(port))
                                )
                                .collect(Collectors.toList());
                pfr.addAllFilteredStdout(filtered);
                if (pfr.filteredStdout().size() == 1) {
                    Matcher m = Pattern.compile(makeRegexForFilteringWindowsNetstatOutput(port))
                            .matcher(pfr.filteredStdout().get(0));
                    if (m.matches()) {
                        pfr.setProcessId(Long.parseLong(m.group(8)));
                        pfr.setReturncode(0);
                    } else {
                        pfr.setMessage(m.toString() + " does not match " + pfr.filteredStdout().get(0));
                        pfr.setReturncode(-2003);
                    }
                } else {
                    pfr.setMessage("pfr.filteredStdout().size=" + pfr.filteredStdout().size());
                    pfr.setReturncode(-2002);
                }
            } else {
                pfr.setMessage("netstat command failed.");
                pfr.addAllStderr(cp.stderr());
                pfr.setReturncode(cp.returncode());
            }
        } else {
            pfr.setMessage(String.format("OSType: %s is unsupported", pfr.ostype().toString()));
            pfr.setReturncode(-999);throw new IllegalStateException("OSType: ${ostype} is unsupported");
        }
        return pfr;
    }

    /**
     * <PRE>
     *   $ netstat -ano | find "LISTEN" | find "80"
     *     TCP         0.0.0.0:13688          0.0.0.0:0              LISTENING       4080
     *     TCP         [::]:13688             [::]:0                 LISTENING       4080
     * </PRE>
     *
     *     protocol    local-address          exteria-address        state     process-id
     *
     * @param port
     * @return
     */
    public static String makeRegexForFilteringWindowsNetstatOutput(int port) {
        return "\\s*TCP\\s+(([\\d\\.]+):(" + String.valueOf(port) + "))\\s+(([\\d\\.]+):(\\d+))\\s+(LISTENING)\\s+(\\d+)\\s*";
    }

    /**
     *
     */
    public static class ProcessFindingResult {
        private final OSType ostype;
        private int port = 0;
        private final List<String> command = new ArrayList<>();
        private final List<String> stdout = new ArrayList<>();
        private final List<String> filteredStdout  = new ArrayList<>();
        private final List<String> stderr  = new ArrayList<>();
        private long processId = -1;  // >0 if found, -1 not found
        private int returncode = -1;  // 0: found, non 0: not found
        private String message = "";
        public ProcessFindingResult(OSType ostype, int port) {
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
