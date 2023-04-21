package com.kazurayam.subprocessj;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.kazurayam.subprocessj.Subprocess.CompletedProcess;

public class CommandLocator {

    /**
     *
     * @param command a command name. e.g, "git"
     * @return the CommandLocatingResult object.
     *    the CommandLocatingResult.command() will return a string which is
     *    the full path of the executable of the command.
     *    e.g, "/usr/local/bin/git"
     */
    public static CommandLocatingResult find(String command) {
        return find(command, null);
    }

    /**
     * see the documentation for detail.
     */
    public static CommandLocatingResult find(String command, Predicate<Path> predicate) {
        Objects.requireNonNull(command);
        // predicate may be null
        if (command.length() == 0) {
            throw new IllegalArgumentException("command must not be 0-length");
        }
        CommandLocatingResult clr = new CommandLocatingResult();
        try {
            CompletedProcess cp;
            if (OSType.isWindows() || OSType.isMac() || OSType.isUnix()) {
                if (OSType.isWindows()) {
                    cp = new Subprocess().run(Arrays.asList("where", command));
                } else {
                    cp = new Subprocess().run(Arrays.asList("which", command));
                }
                clr.addAllStdout(cp.stdout());
                clr.addAllStderr(cp.stderr());
                List<String> lines = normalize(cp.stdout());
                if (lines.size() == 0) {
                    clr.setReturncode(-1);
                } else if (lines.size() == 1) {
                    clr.setReturncode(0);
                    clr.setCommand(cp.stdout().get(0).trim());
                } else {
                    if (predicate != null) {
                        List<String> filtered = lines.stream()
                                .map(Paths::get)
                                .filter(predicate)
                                .map(Path::toString)
                                .collect(Collectors.toList());
                        if (filtered.size() == 0) {
                            clr.setReturncode(-1);
                        } else if (filtered.size() == 1) {
                            clr.setReturncode(0);
                            clr.setCommand(filtered.get(0));
                        } else {
                            clr.setReturncode(-3);
                        }
                    } else {
                        clr.setReturncode(-2);
                    }
                }
            } else {
                clr.setReturncode(-701);
                clr.addAllStderr(Collections.singletonList("unsupported OSType=" + OSType.getOSType()));
            }
        } catch (IOException e) {
            clr.setReturncode(-702);
            clr.addAllStderr(Arrays.asList(e.getMessage()));
        } catch (InterruptedException e) {
            clr.setReturncode(-703);
            clr.addAllStderr(Arrays.asList(e.getMessage()));
        }
        return clr;
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

    /**
     *
     * @param pathFromRoot e.g, Paths.get("C:\\Program Files\\Git\\cmd")
     * @return a Predicate to filter a List of Paths
     */
    public static Predicate<Path> startsWith(String pathFromRoot) {
        final Path base = Paths.get(pathFromRoot);
        Predicate<Path> startsWith = path -> {
            final boolean b = path.startsWith(base);
            return b;
        };
        return startsWith;
    }

    /**
     *
     * @param pathEndingWith e.g, Paths.get("cmd\\git.exe");
     * @return a Predicate to filter a List of Paths
     */
    public static Predicate<Path> endsWith(String pathEndingWith) {
        final Path tail = Paths.get(pathEndingWith);
        Predicate<Path> endsWith = path -> {
            final boolean b = path.endsWith(tail);
            return b;
        };
        return endsWith;
    }

    public static final class CommandLocatingResult {
        private int returncode;
        private final List<String> stdout;
        private final List<String> stderr;
        private String command;
        public CommandLocatingResult() {
            this.returncode = -1;
            this.stdout = new ArrayList<>();
            this.stderr = new ArrayList<>();
            this.command = "";
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
        public void setCommand(String command) {
            this.command = command;
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
        public String command() {
            return command;
        }

        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.print("<command-locating-result ");
            pw.println("rc=\"" + this.returncode() + "\">");
            pw.println("<command>" + this.command() + "</command>");
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
            pw.println("</command-locating-result>");
            pw.flush();
            pw.close();
            return sw.toString();
        }
    }

}
