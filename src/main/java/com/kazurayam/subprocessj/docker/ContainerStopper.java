package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator;
import com.kazurayam.subprocessj.Subprocess;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
import com.kazurayam.subprocessj.docker.model.ContainerId;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <PRE>
 * $ docker stop d4d4a795d76d
 * d4d4a795d76d
 * </PRE>
 */
public class ContainerStopper {

    public ContainerStopper() {}

    public static ContainerStoppingResult stopContainer(
            ContainerId containerId)
            throws IOException, InterruptedException
    {
        Objects.requireNonNull(containerId);
        CommandLocator.CommandLocatingResult clr = DockerCommandLocator.find();
        if (clr.returncode() == 0) {
            String dockerCommand = clr.stdout().get(0).trim();
            List<String> args = Arrays.asList(
                    dockerCommand, "stop", containerId.id()
            );
            CompletedProcess cp =
                    new Subprocess().run(args);
            ContainerStoppingResult csr = new ContainerStoppingResult(cp);
            if (cp.returncode() == 0) {
                csr.setReturncode(0);
            } else {
                csr.setReturncode(cp.returncode());
                csr.setMessage("docker stop command failed");
            }
            return csr;
        } else {
            throw new IllegalStateException(clr.toString());
        }
    }

    public static class ContainerStoppingResult {
        private final CompletedProcess cp;
        private int returncode;
        private String message;

        public ContainerStoppingResult(CompletedProcess cp) {
            this.cp = cp;
            this.returncode = -1;
            this.message = "";
        }
        public void setReturncode(int returncode) {
            this.returncode = returncode;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public int returncode() {
            return this.returncode;
        }
        public String message() {
            return this.message;
        }
        public CompletedProcess completedProcess() {
            return this.cp;
        }
        @Override
        public String toString() {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(new BufferedWriter(sw));
            pw.println("<container-stopping-result rc=\"" + this.returncode() + "\">");
            pw.println("<message>" + this.message() + "</message>");
            pw.print(this.completedProcess().toString());
            pw.println("</container-stopping-result>");
            pw.flush();
            pw.close();
            return sw.toString();
        }
    }


}
