package hishidama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

/**
 * https://www.ne.jp/asahi/hishidama/home/tech/java/process.html#Process
 */
public class StreamGobbler extends Thread {

    private BufferedReader br;

    private List<String> list = new ArrayList<>();

    public StreamGobbler(InputStream is) {
        this(is, StandardCharsets.UTF_8);
    }

    public StreamGobbler(InputStream is, Charset charset) {
        br = new BufferedReader(
                new InputStreamReader(is, charset));
    }

    @Override
    public void run() {
        try {
            for (;;) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                list.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getStringList() {
        return list;
    }
}
