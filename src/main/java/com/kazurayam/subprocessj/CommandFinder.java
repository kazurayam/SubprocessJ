package com.kazurayam.subprocessj;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;

public class CommandFinder {

    public static CommandFindingResult find(String command) {
        Objects.requireNonNull(command);
        if (command.length() == 0) {
            throw new IllegalArgumentException("command must not be 0-length");
        }
        CommandFindingResult cfr = new CommandFindingResult();
        try {
            if (OSType.isWindows()) {
                CompletedProcess cp = new Subprocess().run(Arrays.asList("where", command));
                List<String> normalizedStdout = normalize(cp.stdout());
                if (normalizedStdout.size() > 0) {
                    cfr.setReturncode(0);
                } else {
                    cfr.setReturncode(-1);
                }
                cfr.addAllStdout(cp.stdout());
                cfr.addAllStderr(cp.stderr());
            } else if (OSType.isMac() || OSType.isUnix()) {
                CompletedProcess cp = new Subprocess().run(Arrays.asList("which", command));
                List<String> normalizedStdout = normalize(cp.stdout());
                if (normalizedStdout.size() > 0) {
                    cfr.setReturncode(0);
                } else {
                    cfr.setReturncode(-1);
                }
                cfr.addAllStdout(cp.stdout());
                cfr.addAllStderr(cp.stderr());
            } else {
                cfr.setReturncode(-701);
                cfr.addAllStderr(Arrays.asList("unsupported OSType=" + OSType.getOSType()));
            }
        } catch (IOException e) {
            cfr.setReturncode(-702);
            cfr.addAllStderr(Arrays.asList(e.getMessage()));
        } catch (InterruptedException e) {
            cfr.setReturncode(-703);
            cfr.addAllStderr(Arrays.asList(e.getMessage()));
        }
        return cfr;
    }

    public static CommandFindingResult which(String command) {
        return find(command);
    }

    public static CommandFindingResult where(String command) {
        return find(command);
    }

    /**
     * chomp of empty lines
     */
    private static List<String> normalize(List<String> messages) {
        List<String> result = new ArrayList<>();
        for (String line : messages) {
            String s = line.trim();
            if (s.length() > 0) {
                result.add(s);
            }
        }
        return result;
    }

    public static final class CommandFindingResult {
        private int returncode;
        private final List<String> stdout;
        private final List<String> stderr;
        public CommandFindingResult() {
            this.returncode = -1;
            this.stdout = new ArrayList<>();
            this.stderr = new ArrayList<>();
        }
        public void setReturncode(int returncode) {
            this.returncode = returncode;
        }
        public void addAllStdout(List<String> stdout) {
            this.stdout.addAll(stdout);
        }
        public void addAllStderr(List<String> stderr) {
            this.stderr.addAll(stderr);
        }
        public int returncode() {
            return returncode;
        }
        public List<String> stdout() {
            return stdout;
        }
        public List<String> stderr() {
            return stderr;
        }

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.print("<cfr ");
            if (this.returncode() == 0) {
                pw.println("rc=\"" + this.returncode() + "\">");
            } else {
                pw.println("rc=\"" + this.returncode() + "\">");
            }
            pw.print("<stdout>");
            int count = 0;
            for (String s : this.stdout()) {
                if (count > 0) pw.println();
                pw.print(s);
                count += 1;
            }
            pw.println("</stdout>");
            pw.print("<stderr>");
            for (String s : this.stderr()) {
                if (count > 0) pw.println();
                pw.print(s);
                count += 1;
            }
            pw.println("</stderr>");
            pw.println("<cfr>");
            pw.flush();
            pw.close();
            return sw.toString();
        }
    }

}
