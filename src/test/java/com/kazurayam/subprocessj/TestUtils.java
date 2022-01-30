package com.kazurayam.subprocessj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtils {

    /**
     *
     * @param is InputStream
     * @return text content from the InputStream
     * @throws IOException
     */
    protected static String readInputStream(InputStream is) throws IOException {
        BufferedReader r =
                new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = r.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

}
