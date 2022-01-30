package hishidama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {

    public static void printInputStream(InputStream is) throws IOException {
        BufferedReader r =
                new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void printProcessOutput(String label, Process process) throws IOException {
        System.out.println(String.format("-------- %s --------", label));
        System.out.println("[STDOUT]");
        printInputStream(process.getInputStream());
        System.out.println("[/STDOUT]");
        System.out.println("[STDERR]");
        printInputStream(process.getErrorStream());
        System.out.println("[/STDERR]");
    }

}
