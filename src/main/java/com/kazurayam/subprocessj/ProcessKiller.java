package com.kazurayam.subprocessj;

import java.io.IOException;
import java.util.Arrays;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;

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
 * on Winodws? TODO later
 *
 */
public class ProcessKiller {

    private ProcessKiller() {}

    /**
     *
     * @param portNumber the IP port number on which the Process is hanging on; you want to kill that process.
     * @return the ID of the process (&gt;0) which is listening to the IP portNumber
     * and therefore killed; returns -1 if the process was not found.
     * @throws IOException if failed to kill a OS process
     * @throws InterruptedException if the process was interrupted
     */
    public static Long killProcessOnPort(Integer portNumber)
            throws IOException, InterruptedException
    {
        Long processId = findProcessIdOnPort(portNumber);
        if (processId > 0) {
            processId = killProcessById(processId);
            if (processId > 0) {
                return processId;
            } else {
                return -1L;
            }
        } else {
            return -1L;
        }
    }

    /**
     * find the process which is listening to the port at the IP portNumber
     * @param portNumber
     * @return the process ID, -1 if not found
     */
    static Long findProcessIdOnPort(Integer portNumber)
            throws IOException, InterruptedException {
        if (OSValidator.isMac()) {
            return findProcessIdOnPort_Mac(portNumber);
        } else if (OSValidator.isUnix()) {
            return findProcessIdOnPort_Unix(portNumber);
        } else {
            throw new UnsupportedOperationException("TODO");
        }
    }

    static Long findProcessIdOnPort_Unix(Integer portNumber)
            throws IOException, InterruptedException
    {
        return findProcessIdOnPort_Mac(portNumber);
    }

    static Long findProcessIdOnPort_Mac(Integer portNumber)
            throws IOException, InterruptedException
    {
        if (portNumber <= 0) {
            throw new IllegalArgumentException("portNumber must be >0");
        }
        Subprocess subprocess = new Subprocess();
        CompletedProcess cp =
                subprocess.run(Arrays.asList("lsof", "-i:" + portNumber, "-P"));
        if (cp.returncode() == 0 &&
                cp.stdout().size() == 2) {
            /*
$ lsof -i:8500 -P
COMMAND  PID           USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
java    4830 kazuakiurayama    6u  IPv6 0xce0db8744e9dc403      0t0  TCP *:8500 (LISTEN)
             */
            String line = cp.stdout().get(1);
            String[] items = line.split("\\s+");
            return Long.valueOf(items[1]);
        } else {
            cp.stderr().forEach(System.out::println);
            return -1L;
        }
    }

    static Long killProcessById(Long processId) throws IOException, InterruptedException {
        if (processId <= 0) {
            throw new IllegalArgumentException("processId must be > 0");
        }
        Subprocess subprocess = new Subprocess();
        CompletedProcess cp = subprocess.run(
                Arrays.asList("kill", String.valueOf(processId)));
        if (cp.returncode() == 0) {
            return processId;
        } else {
            cp.stdout().forEach(System.out::println);
            return -1L;
        }


    }

}

