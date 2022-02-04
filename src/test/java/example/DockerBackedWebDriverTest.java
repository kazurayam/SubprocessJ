package example;

import com.kazurayam.subprocessj.docker.ContainerFinder;
import com.kazurayam.subprocessj.docker.ContainerFinder.ContainerFindingResult;
import com.kazurayam.subprocessj.docker.ContainerRunner;
import com.kazurayam.subprocessj.docker.ContainerRunner.ContainerRunningResult;
import com.kazurayam.subprocessj.docker.ContainerStopper;
import com.kazurayam.subprocessj.docker.ContainerStopper.ContainerStoppingResult;
import com.kazurayam.subprocessj.docker.model.ContainerId;
import com.kazurayam.subprocessj.docker.model.DockerImage;
import com.kazurayam.subprocessj.docker.model.PublishedPort;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A JUnit5 test case.
 * This test visits and tests a URL "http://127.0.0.1:3080/" using Selenium WebDriver.
 * The URL is served by a process in which a Docker Container runs
 * using a docker image which kazurayam published.
 * The web app was originally developed in Python language by the Pallets project,
 * is published at
 * - https://flask.palletsprojects.com/en/2.0.x/tutorial/
 *
 * This test automates running and stopping a Docker Container process
 * using commandline commands: `docker run` and `docker stop`.
 *
 * The `com.kazurayam.subprocessj.docker.ContainerRunner` class wraps the "docker run" command.
 * The `com.kazurayam.subprocessj.docker.ContainerFinder` class wraps the "docker ps" command.
 * The `com.kazurayam.subprocessj.docker.ContainerStopper` class wraps the "docker stop" command.
 *
 * These classes call `java.lang.ProcessBuilder` to execute the `docker` command from Java.
 * The `com.kazurayam.subprocessj.Subprocess` class wraps the `ProcessBuilder` and provides a simpler API.
 *
 * @author kazurayam
 */
public class DockerBackedWebDriverTest {

    private static final int HOST_PORT = 3080;

    private static final PublishedPort publishedPort = new PublishedPort(HOST_PORT, 8080);
    private static final DockerImage image = new DockerImage("kazurayam/flaskr-kazurayam:1.1.0");

    private WebDriver driver = null;

    /**
     * start a Docker Container by "docker run" command.
     * In the container, a web server application runs to server a URL http://127.0.0.1:3080/
     *
     * It takes a bit long time; approximately 5 seconds. Just wait!
     */
    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        File directory = Files.createTempDirectory("DockerBackedWebDriverTest").toFile();
        ContainerRunningResult crr =
                ContainerRunner.runContainerAtHostPort(directory, publishedPort, image);
        if (crr.returncode() != 0) {
            throw new IllegalStateException(crr.toString());
        }
        // setup ChromeDriver
        WebDriverManager.chromedriver().setup();
    }

    /**
     * open a Chrome browser window
     */
    @BeforeEach
    public void beforeEach() {
        driver = new ChromeDriver();
    }

    /**
     * Test an HTML page.
     * Will verify if the site name in the page header is "Flaskr".
     */
    @Test
    public void test_page_header() {
        driver.navigate().to(String.format("http://127.0.0.1:%d/", HOST_PORT));
        WebElement siteName = driver.findElement(By.xpath("/html/body/nav/h1"));
        assertNotNull(siteName);
        assertEquals("Flaskr", siteName.getText());
        delay(2000);
    }

    /**
     * close the Chrome browser window
     */
    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }


    /**
     * Stop the Docker Container gracefully by the "docker stop" command.
     * It will take approximately 10 seconds.
     * Be tolerant. Just wait!
     */
    @AfterAll
    public static void afterAll() throws IOException, InterruptedException {
        ContainerFindingResult cfr = ContainerFinder.findContainerByHostPort(HOST_PORT);
        if (cfr.returncode() == 0) {
            ContainerId containerId = cfr.containerId();
            ContainerStoppingResult csr = ContainerStopper.stopContainer(containerId);
            if (csr.returncode() != 0) {
                throw new IllegalStateException(csr.toString());
            }
        } else {
            throw new IllegalStateException(cfr.toString());
        }
    }

    private void printResult(String label, ContainerFindingResult cfr) {
        System.out.println("-------- " + label + " --------");
        System.out.println(cfr.toString());
    }

    private void delay(int millis) {
        try {
            long l = (long)millis;
            Thread.sleep(l);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

}
