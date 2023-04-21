package com.kazurayam.subprocessj;

import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.kazurayam.subprocessj.CommandLocator.CommandLocatingResult;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * [pngquant](https://pngquant.org/) is a command-line utility for lossy compression of PNG images.
 * It is available on Mac, Windows and Linux.
 */
public class PngquantTest {

    private Path fixturesDir = Paths.get(".").resolve("src/test/fixtures");
    private Path outputDir = Paths.get(".").resolve("build/tmp/testOutput/PngquantTest");

    @BeforeEach
    public void beforeEach() throws IOException {
        Files.createDirectories(outputDir);
    }

    /**
     * Here I assume that "pngquant" is already installed in the runtime environment and
     * the command "$ pngquant --version" responds
     * "2.18.0 (January 2023)"
     *
     * This code shows how to execute the pngquant from Java to compress
     * a sample PNG image using pngquant.
     */
    @Test
    public void test_compress_png_using_pngquant() throws IOException {
        // 1. make sure the source PNG image is present
        Path sourcePng = fixturesDir.resolve("apple.png");
        assertTrue(Files.exists(sourcePng));

        // 2. copy the source to the target file
        Path targetPng = outputDir.resolve("apple.png");
        Files.copy(sourcePng, targetPng, StandardCopyOption.REPLACE_EXISTING);

        // 3. record the size information of the target file
        long sizeBeforeCompression = targetPng.toFile().length();

        // 4. check if "pngquant" is installed and available
        CommandLocator.CommandLocatingResult clr = CommandLocator.find("pngquant");
        if (clr.returncode() == 0) {
            // 5. now compress it using pngquant
            Subprocess.CompletedProcess cp;
            try {
                cp = new Subprocess().run(Arrays.asList(
                        "pngquant", "--ext", ".png", "--force",
                        "--speed", "1", targetPng.toString()
                ));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 6. assert that pngquant ran successfully
            System.out.println("[test_compress_png_using_pngquant]");
            System.out.println(cp.toString());
            assertEquals(0, cp.returncode());
        }

        // 7. record the size information of the compressed file
        long sizeAfterCompression = targetPng.toFile().length();

        // 8. report the result
        System.out.println(String.format("file: %s", targetPng.toString()));
        System.out.println(String.format("size before compression: %d", sizeBeforeCompression));
        System.out.println(String.format("size after compression: %d", sizeAfterCompression));
        long delta = ((sizeBeforeCompression - sizeAfterCompression) * 100) / sizeBeforeCompression;
        System.out.println(String.format("size delta: Δ%d%%", delta));
    }

}
