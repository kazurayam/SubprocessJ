package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.docker.model.PublishedPort;
import com.kazurayam.subprocessj.Subprocess;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
import com.kazurayam.subprocessj.docker.model.DockerImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Execute `docker run` to run a contaner as a daemon specifying port forwarding and
 * docker image
 *
 * <PRE>
 *     $ docker run -d -p 80:8080 kazurayam/flaskr-kazurayam:1.1.0
 * </PRE>
 */
public class ContainerRunner {

    public ContainerRunner() {}

    public static ContainerRunningResult runContainerAtHostPort(
            File directory, PublishedPort publishedPort, DockerImage image) throws IOException, InterruptedException {
        Objects.requireNonNull(directory);
        if (! directory.exists()) {
            throw new IllegalArgumentException(directory.toString() + " does not exist");
        }
        Objects.requireNonNull(publishedPort);
        Objects.requireNonNull(image);
        //
        CommandLocatingResult commfr = DockerCommandFinder.find();
        if (commfr.returncode() == 0) {
            String dockerCommand = commfr.stdout().get(0).trim();  //  e.g, "/usr/local/bin/docker"
            List<String> args = Arrays.asList(
                    dockerCommand, "run", "-d",
                    "-p", publishedPort.hostPort() + ":" + publishedPort.containerPort(),
                    image.toString()
            );
            CompletedProcess cp =
                    new Subprocess().cwd(directory).run(args);
            ContainerRunningResult crr = new ContainerRunningResult(cp);
            if (cp.returncode() == 0) {
                crr.setReturncode(0);
            } else {
                crr.setReturncode(cp.returncode());
                crr.setMessage("docker run command failed");
            }
            return crr;
        } else {
            throw new IllegalStateException(commfr.toString());
        }
    }

    public static class ContainerRunningResult {
        private final CompletedProcess cp;
        private int returncode;
        private String message;

        public ContainerRunningResult(CompletedProcess cp) {
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
            pw.println("<container-running-result rc=\"" + this.returncode() + "\">");
            pw.println("<message>" + this.message() + "</message>");
            pw.print(this.completedProcess().toString());
            pw.println("</container-running-result>");
            pw.flush();
            pw.close();
            return sw.toString();
        }
    }
}
