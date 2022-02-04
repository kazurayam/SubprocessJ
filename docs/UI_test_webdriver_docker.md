# Automated UI Test using WebDriver backed by Docker Container

@author kazurayam
@date 4 Feb, 2022

## Problem to solve

## Solution

I have developed a code in Java using JUnit 5, which does the following:

-   This test visits and tests a URL "http://127.0.0.1:3080/" using Selenium WebDriver.

-   The URL is served by a process on the localhost, in which a Docker Container runs using a docker image which kazurayam published.

-   The web app was originally developed in Python language by the Pallets project, is published at [flaskr tutorial](https://flask.palletsprojects.com/en/2.0.x/tutorial/)

-   This test automates running and stopping a Docker Container process using commandline commands: `docker run`, `docker ps` and `docker stop`.

-   The [`com.kazurayam.subprocessj.docker.ContainerRunner`](https://github.com/kazurayam/subprocessj/blob/master/src/main/java/com/kazurayam/subprocessj/docker/ContainerRunner.java) class wraps the "docker run" command.

-   The [`com.kazurayam.subprocessj.docker.ContainerFinder`](https://github.com/kazurayam/subprocessj/blob/master/src/main/java/com/kazurayam/subprocessj/docker/ContainerFinder.java) class wraps the "docker ps" command.

-   The [`com.kazurayam.subprocessj.docker.ContainerStopper`](https://github.com/kazurayam/subprocessj/blob/master/src/main/java/com/kazurayam/subprocessj/docker/ContainerStopper.java) class wraps the "docker stop" command.

-   These classes call [`java.lang.ProcessBuilder`](https://www.baeldung.com/java-lang-processbuilder-api) to execute the `docker` command from Java.

-   The [`com.kazurayam.subprocessj.Subprocess`](https://github.com/kazurayam/subprocessj/blob/master/src/main/java/com/kazurayam/subprocessj/Subprocess.java) class wraps the `ProcessBuilder` and provides a simple API for Java application with work with OS commands.

## Description

### Sequence diagram

The following diagram shows the sequence.

![sequence](diagrams/out/DockerBackedWebDriverTest_sequence.png)

### Code

-   [example.DockerBackedWebDriverTest](https://github.com/kazurayam/subprocessj/blob/master/src/test/java/example/DockerBackedWebDriverTest.java)

<!-- -->

    link:https://github.com/kazurayam/subprocessj/blob/master/src/test/java/example/DockerBackedWebDriverTest.java[DeockerBackedWebDriverTest]

## How to reuse this

See [build.gradle](https://github.com/kazurayam/subprocessj/blob/master/build.gradle)

## Conclusion

I wanted to perform automated Web UI testings written in Java/Groovy against a web application written in Python. The [Subprocessj 0.3.0](https://mvnrepository.com/artifact/com.kazurayam/subprocessj) library made it possible for me. I am contented with it.

## References

-   [forum.docker.com, “docker run” cannot be killed with ctrl+c](https://forums.docke.com/t/docker-run-cannot-be-killed-with-ctrl-c/13108/)

-   [docker ps command](https://matsuand.github.io/docs.docker.jp.onthefly/engine/reference/commandline/ps/)

-   [docker run command](https://docs.docker.com/engine/reference/commandline/run/)

-   [docker stop command](https://matsuand.github.io/docs.docker.jp.onthefly/engine/reference/commandline/stop/)

-   [subprocessj project top](https://github.com/kazurayam/subprocessj)
