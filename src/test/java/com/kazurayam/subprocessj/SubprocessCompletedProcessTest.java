package com.kazurayam.subprocessj;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * will test Subprocess.CompleteProcess class
 */
public class SubprocessCompletedProcessTest {

    @Test
    public void test_quote() {
        assertEquals("123", Subprocess.CompletedProcess.quote("123"));
        assertEquals("abc", Subprocess.CompletedProcess.quote("abc"));
        assertEquals("'123&abc'", Subprocess.CompletedProcess.quote("123&abc"));
        assertEquals("'\\''", Subprocess.CompletedProcess.quote("'"));
        assertEquals("'\"abc\"'", Subprocess.CompletedProcess.quote("\"abc\""));
        assertEquals("'a(b)'", Subprocess.CompletedProcess.quote("a(b)"));
        assertEquals("'a;b'", Subprocess.CompletedProcess.quote("a;b"));
        assertEquals("a=b", Subprocess.CompletedProcess.quote("a=b"));
    }

    @Test
    public void testSmoke() {
        String prefix = "20230828_201457";
        String htmlFileName = prefix + ".html";
        String pdfFileName = prefix + ".pdf";
        List<String> args = Arrays.asList(
                "/usr/local/bin/wkhtmltopdf",
                "--debug-javascript",
                "--run-script", "console.log('starting')",
                "--run-script", "document.querySelector('h1').textContent='Hello'",
                "--run-script", "console.log('finished')",
                "--javascript-delay", "1000",
                htmlFileName, pdfFileName
        );
        Subprocess.CompletedProcess cp = new Subprocess.CompletedProcess(args);
        System.out.println(cp.toString());
    }
}
