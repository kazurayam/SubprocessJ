package com.kazurayam.subprocessj.docker;

import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;
import com.kazurayam.subprocessj.docker.model.NameValuePair;
import com.kazurayam.subprocessj.docker.model.PublishedPort;
import com.kazurayam.subprocessj.Subprocess;
import com.kazurayam.subprocessj.Subprocess.CompletedProcess;
import com.kazurayam.subprocessj.docker.model.DockerImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
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

    private final DockerImage image;
    private final File directory;
    private final PublishedPort publishedPort;
    private final List<NameValuePair> envVars;

    public ContainerRunner(Builder builder) {
        this.image = builder.image;
        this.directory = builder.directory;
        this.publishedPort = builder.publishedPort;
        this.envVars = builder.envVars;
    }

    public ContainerRunningResult run() throws IOException, InterruptedException
    {
        CommandLocatingResult clr = DockerCommandLocator.find();
        if (clr.returncode() == 0) {
            String dockerCommand = clr.command();  //  e.g, "/usr/local/bin/docker"

            // construct the list of arguments to the command
            List<String> args = new ArrayList<>();
            args.addAll(Arrays.asList(dockerCommand, "run", "-d"));
            // environment variables if specified
            for (NameValuePair nameValuePair : envVars) {
                args.add("-e");
                args.add(nameValuePair.toString());
            }
            // IP port mapping if specified
            if (publishedPort != PublishedPort.NULL_OBJECT) {
                args.add("-p");
                args.add(publishedPort.hostPort() + ":" + publishedPort.containerPort());
            }
            args.add(image.toString());

            // now run the command
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
            throw new IllegalStateException(clr.toString());
        }
    }

    /**
     * Employing the "Builder" pattern of "Effective Java"
     */
    public static class Builder {
        // required params
        private final DockerImage image;
        // optional params
        private File directory;
        private PublishedPort publishedPort;
        private final List<NameValuePair> envVars;
        //
        public Builder(DockerImage image) throws IOException {
            this.image = image;
            this.directory = Files.createTempDirectory("ContainerRunner.Builder").toFile();
            this.publishedPort = PublishedPort.NULL_OBJECT;
            this.envVars = new ArrayList<NameValuePair>();
        }
        public Builder directory(File directory) throws IOException {
            Objects.requireNonNull(directory);
            if (! directory.exists()) {
                throw new IOException(directory + " does not exist");
            }
            this.directory = directory;
            return this;
        }
        public Builder publishedPort(PublishedPort publishedPort) {
            Objects.requireNonNull(publishedPort);
            this.publishedPort = publishedPort;
            return this;
        }
        public Builder envVar(NameValuePair nameValuePair) {
            Objects.requireNonNull(nameValuePair);
            this.envVars.add(nameValuePair);
            return this;
        }
        public Builder addEnvVars(List<NameValuePair> nameValuePairs) {
            Objects.requireNonNull(nameValuePairs);
            this.envVars.addAll(nameValuePairs);
            return this;
        }
        public ContainerRunner build() {
            return new ContainerRunner(this);
        }
    }

    /**
     *
     */
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
