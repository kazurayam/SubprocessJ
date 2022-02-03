package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.Subprocess;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
import com.kazurayam.subprocessj.docker.model.ContainerId;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Execute `docker ps` command to find out the id of container that is
 * publishing the specified IP port
 *
 * <PRE>
 * $ docker ps --filter publish=80 --filter status=running -q
 * fd5ad3b76b13
 * </PRE>
 *
 * --filter publish=portNumber : require the container which publishes the portNumber
 * --filter status=running : require the container which is running
 * -q : show container-id only
 *
 * Reference:
 * - https://matsuand.github.io/docs.docker.jp.onthefly/engine/reference/commandline/ps/
 */
public class ContainerFinder {

    public ContainerFinder() {}

    public static ContainerFindingResult findContainerByHostPort(int hostPort)
            throws IOException, InterruptedException
    {
        CommandLocatingResult clr = DockerCommandLocator.find();
        if (clr.returncode() == 0) {
            String dockerCommand = clr.stdout().get(0).trim();
            List<String> args = Arrays.asList(
                    dockerCommand, "ps",
                    "--filter", "publish=" + hostPort,
                    "--filter", "status=running",
                    "-q"
            );
            CompletedProcess cp = new Subprocess().run(args);
            ContainerFindingResult cfr = new ContainerFindingResult(cp);
            if (cp.returncode() == 0) {
                if (cp.stdout().size() == 1) {
                    ContainerId containerId = new ContainerId(cp.stdout().get(0).trim());
                    cfr.setContainerId(containerId);
                    cfr.setReturncode(0);
                } else {
                    cfr.setMessage("no container found; or 2 or more containers found.");
                    cfr.setReturncode(-1);
                }
            } else {
                cfr.setMessage("docker ps command failed somehow");
                cfr.setReturncode(cp.returncode());
            }
            return cfr;
        } else {
            throw new IllegalStateException("docker command is not installed; " + clr.toString());
        }
    }


    public static class ContainerFindingResult {
        private final CompletedProcess cp;
        //
        private int returncode = -1;
        private String message = "";
        private ContainerId containerId = ContainerId.NULL_OBJECT;
        public ContainerFindingResult(CompletedProcess cp) {
            this.cp = cp;
        }
        public void setReturncode(int returncode) {
            this.returncode = returncode;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public void setContainerId(ContainerId found) {
            this.containerId = found;
        }
        public int returncode() {
            return this.returncode;
        }
        public String message() {
            return this.message;
        }
        public ContainerId containerId() {
            return this.containerId;
        }
        public CompletedProcess completedProcess() { return this.cp; }
        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(new BufferedWriter(sw));
            pw.println("<container-finding-result rc=\"" + this.returncode() + "\">");
            pw.println("<message>" + this.message() + "</message>");
            pw.println("<containerId>" + this.containerId() + "</containerId>");
            pw.print(this.completedProcess().toString());
            pw.println("</container-finding-result>");
            pw.flush();
            pw.close();
            return sw.toString();
        }
    }
}
